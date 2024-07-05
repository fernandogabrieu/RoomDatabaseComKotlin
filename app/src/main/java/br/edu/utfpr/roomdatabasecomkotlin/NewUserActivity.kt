package br.edu.utfpr.roomdatabasecomkotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.utfpr.roomdatabasecomkotlin.database.AppDatabase
import br.edu.utfpr.roomdatabasecomkotlin.database.daos.UserDao
import br.edu.utfpr.roomdatabasecomkotlin.database.models.User
import br.edu.utfpr.roomdatabasecomkotlin.databinding.ActivityNewUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewUserActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewUserBinding
    private lateinit var database : AppDatabase
    private lateinit var userDao : UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.database = AppDatabase.getInstance(this)
        this.userDao = this.database.userDao()
    }

    override fun onStart() {
        super.onStart()

        this.binding.btnSave.setOnClickListener {

            // Temos que criar uma Coroutine pois não podemos chamar funções que interajam com o BD
            // (nesse caso a função saveUser que utiliza o insert do DAO) na Main thread, então chamamos
            // uma Coroutine com o contexto de IO (que é uma thread paralela) e salvamos os dados no BD
            // com saveUser()
            CoroutineScope(Dispatchers.IO).launch {
                val result = saveUser(
                    binding.edtFirstName.text.toString(),
                    binding.edtLastName.text.toString()
                )

                // Em seguida voltamos para a thread principal para poder atualizar a interface
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        this@NewUserActivity,
                        if(result)"User saved!" else "Error trying to save user",
                        Toast.LENGTH_LONG
                    ).show()

                    if(result)
                        finish()
                }
            }
        }
    }

    private suspend fun saveUser(firstName: String, lastName: String): Boolean{
        //Validando se os campos não estão vazios e se não são só espaços
        if(firstName.isBlank() || firstName.isEmpty())
            return false

        if(lastName.isBlank() || lastName.isEmpty())
            return false

        //Se estiver tudo ok, salva os dados no Database e retorna true
        this.userDao.insert(User(firstName, lastName))

        return true
    }
}