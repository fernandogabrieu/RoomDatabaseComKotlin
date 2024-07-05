package br.edu.utfpr.roomdatabasecomkotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.utfpr.roomdatabasecomkotlin.database.AppDatabase
import br.edu.utfpr.roomdatabasecomkotlin.database.daos.UserDao
import br.edu.utfpr.roomdatabasecomkotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var database : AppDatabase
    private lateinit var userDao : UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.database = AppDatabase.getInstance(this)
        this.userDao = this.database.userDao()

    }

    override fun onStart() {
        super.onStart()

        loadTotalUsers()

        this.binding.btnNewUser.setOnClickListener {
            btnNewUserOnClick()
        }
    }

    private fun loadTotalUsers() {
        this.binding.tvInfoTotalUsers.text = "Carregando..."
        //Como vamos acessar o BD, vamos usar uma coroutine como thread paralela
        CoroutineScope(Dispatchers.IO).launch {
            val total = userDao.getTotalItems()

            //Após coletar a informação do BD, voltamos para a thread Main
            withContext(Dispatchers.Main){
                binding.tvInfoTotalUsers.text = "Total de usuários: $total"
            }
        }
    }

    private fun btnNewUserOnClick() {
        val intent = Intent(this, NewUserActivity::class.java)
        startActivity(intent)
    }
}