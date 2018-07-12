package com.example.harsha.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import android.widget.Toast
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.R.attr.data
import com.example.harsha.myapplication.R.id.fab
import com.example.harsha.myapplication.R.string.sign_out


//import android.support.annotation.NonNull
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.android.gms.tasks.Task


const val EXTRA_MESSAGE = "com.example.harsha.MESSAGE"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "EmailPassword"
    private val mStatusTextView: TextView? = null
    private val REQUEST_CODE_1 = 1
    private var mAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 9001


    private var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mAuth = FirebaseAuth.getInstance()


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            signIn("videoatom@gmail.com", "Comp123!")
        }

        // Example of a call to a native method
        sample_text.text = stringFromJNI()

        sign_in_button.setOnClickListener(this)

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance()
        // [END initialize_auth]
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            sample_text.text = sign_out.toString()
            sign_in_button.visibility = View.VISIBLE;
            sign_out_and_disconnect.visibility = View.GONE;
            return
        }
        val intent = Intent(this, SubActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, currentUser?.email)
        }
        startActivityForResult(intent, REQUEST_CODE_1)
    }

    // [END on_start_check_user]
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        // [START sign_in_with_email]
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success : " + mAuth?.currentUser!!.email)
                val user = mAuth?.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(this@MainActivity, R.string.auth_failed,
                        Toast.LENGTH_SHORT).show()
                updateUI(null)
            }

            // [START_EXCLUDE]
            if (!task.isSuccessful) mStatusTextView?.setText(R.string.auth_failed)
            // [END_EXCLUDE]
        }
        // [END sign_in_with_email]
    }

    private fun validateForm(): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                sign_in_button.visibility = View.GONE;
                sign_out_and_disconnect.visibility = View.VISIBLE;
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                sample_text.text = account.email


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        } else {
            Log.d(TAG, "signOutWithEmail:signout : " + mAuth?.currentUser?.email)
            sample_text.apply {
                text =  mAuth?.currentUser?.email
            }
            mAuth?.signOut()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sign_in_button -> {
                signInGoogle()
            }
            R.id.sign_out_and_disconnect -> {
                signOutGoogle()
            }
            R.id.disconnect_button -> {
                revokeAccess();
            }
        }
    }

    private fun signOutGoogle() {
        mAuth?.signOut()

        // Google sign out
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this) { updateUI(null) }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun revokeAccess() {
        // Firebase sign out
        mAuth?.signOut()

        // Google revoke access
        mGoogleSignInClient?.revokeAccess()?.addOnCompleteListener(this
        ) { updateUI(null) }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
