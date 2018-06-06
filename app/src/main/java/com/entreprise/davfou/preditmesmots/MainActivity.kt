package com.entreprise.davfou.preditmesmots

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private var mDB: Database?= null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private var mUIHandler = Handler()
    private var myWordList = mutableListOf<Data>()
    private var hint = mutableListOf("", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()

        mDB = Database.getInstance(this)

        //readCSVFirstTime()
        initFromDB()

        val rootView: ViewGroup = findViewById(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r);

            val heightDiff = rootView.rootView.height - (r.bottom - r.top);
            if (heightDiff > rootView.rootView.height / 4) {
                layoutBtn.visibility= View.VISIBLE;
            }else{
                layoutBtn.visibility= View.GONE;

            }
        }

        edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                val task = Runnable {

                    var lenghtText: Int = p0.toString().length

                    //devoir récupérer le dernier mot rentré

                    if (lenghtText > 0) {
                        //On test si un espace est tapé
                        if (p0.toString().get(lenghtText - 1).isWhitespace()) {
                            //On doit afficher les 3 mots à prédire
                            println("space detect")
                            var ss = p0.toString().substring(0, lenghtText - 1)
                            var words = ss.split(" ")
                            var test = words[words.size - 1]
                            var top3 = getTop3Hint(test)
                            // If i get 3 hints for the word typed
                            if (top3.size == 0) {
                                insertInDB(Data(null, words[words.size - 2], test, getHintCount(words[words.size - 2], test)))
                                myWordList.add(Data(null, words[words.size - 2], test, getHintCount(words[words.size - 2], test)))
                            } else {
                                // if the word typed is not the first word
                                if (words.size > 1) {
                                    // if pair word1/word2 found in DB
                                    if (isComboFound(words[words.size - 2], test)) {
                                        // update count of hint in DB
                                        insertInDB(Data(null, words[words.size - 2], test, getHintCount(words[words.size - 2], test) + 1))
                                        myWordList[getIndexComboFound(words[words.size - 2], test)].count++
                                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                                            hintUpdateUI(top3)
                                        })
                                    } else {
                                        // add the pair to DB
                                        insertInDB(Data(null, words[words.size - 2], test, getHintCount(words[words.size - 2], test)))
                                        myWordList.add(Data(null, words[words.size - 2], test, getHintCount(words[words.size - 2], test)))
                                    }
                                    // if the word typed is the first word
                                } else {
                                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                                        hintUpdateUI(top3)
                                    })
                                }
                            }
                        }
                    }
                }
                mDbWorkerThread.postTask(task)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })




        button1.setOnClickListener(View.OnClickListener {
            clickedOnButton1();
        })

        button2.setOnClickListener(View.OnClickListener {
            clickedOnButton2();
        })

        button3.setOnClickListener(View.OnClickListener {
            clickedOnButton3();
        })


    }

    fun hintUpdateUI(top3: List<String>){
        for ((index, value)in top3.withIndex()){
            hint[index] = value
        }
        button1.text = hint.get(0)
        button2.text = hint.get(1)
        button3.text = hint.get(2)
    }

    fun isComboFound(word1: String, word2: String): Boolean{
        var isFound = false
        for (item in myWordList){
            if (item.word1 == word1 && item.word2 == word2){
                isFound = true
                break
            }
        }
        return isFound
    }

    fun getIndexComboFound(word1: String, word2: String): Int{
        var indexFound = 0
        for ((index, item) in myWordList.withIndex()){
            if (item.word1 == word1 && item.word2 == word2){
                indexFound = index
                break
            }
        }
        return indexFound
    }

    fun getHintCount(word1: String, word2: String): Int{
        var count = 1
        for (item in myWordList){
            if (item.word1 == word1 && item.word2 == word2){
                count = item.count
                break
            }
        }
        return count
    }

    fun getTop3Hint(word: String): List<String>{
        var top3List = mutableListOf<Data>()
        for (item in myWordList){
            if (item.word1 == word){
                println(item.word1+", "+item.word2+", "+item.count)
            }
            if (item.word1 == word){
                top3List.add(Data(null, item.word1, item.word2, item.count))
            }
            top3List.sortByDescending {it.count}
        }
        var top3Word = mutableListOf<String>()
        for (item in top3List.take(3)){
            top3Word.add(item.word2)
        }
        return top3Word
    }

    fun clickedOnButton1(){
        var toto : String  = edittext.text.toString() + hint.get(0);
        edittext.text=Editable.Factory.getInstance().newEditable(toto);
        edittext.setSelection(edittext.text.toString().length)
    }


    fun clickedOnButton2(){
        var toto : String  = edittext.text.toString() + hint.get(1);
        edittext.text=Editable.Factory.getInstance().newEditable(toto);
        edittext.setSelection(edittext.text.toString().length)
    }


    fun clickedOnButton3(){
        var toto : String  = edittext.text.toString() + hint.get(2);
        edittext.text=Editable.Factory.getInstance().newEditable(toto);
        edittext.setSelection(edittext.text.toString().length)
    }

    private fun initFromDB(){
        val task = Runnable {
            val data = mDB?.DataDAO()?.getAll()
            println("Datasize : "+data?.size)
            myWordList = (data as MutableList<Data>?)!!
        }
        mDbWorkerThread.postTask(task)
    }

    private fun insertInDB(data: Data){
        mDB?.DataDAO()?.insert(data)
    }

    private fun insertInDBWorkerThread(data: Data){
        val task = Runnable { mDB?.DataDAO()?.insert(data) }
        mDbWorkerThread.postTask(task)
    }

    fun readCSVFirstTime() {
        var fileReader: BufferedReader? = null

        try {
            val datas = ArrayList<Data>()
            var line: String?

            //var path = Uri.parse("android.resource://com.entreprise.davfou.preditmesmots/" + R.raw.gram).path
            //println(path)

            //fileReader = BufferedReader(FileReader(path))
            fileReader = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.gram)))

            // Read CSV header
            fileReader.readLine()

            // Read the file line by line starting from the second line
            line = fileReader.readLine()
            while (line != null) {
                val tokens = line.split(",")
                if (tokens.size > 0) {
                    val data = Data(
                            null,
                            tokens[0],
                            tokens[1],
                            Integer.parseInt(tokens[2]))
                    datas.add(data)
                    insertInDBWorkerThread(data)
                }

                line = fileReader.readLine()
            }

            // Print the new customer list
            for (data in datas) {
                println(data)
            }
        } catch (e: Exception) {
            println("Reading CSV Error!")
            e.printStackTrace()
        } finally {
            try {
                fileReader?.close()
            } catch (e: IOException) {
                println("Closing fileReader Error!")
                e.printStackTrace()
            }
        }

    }
}
