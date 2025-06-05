package com.transporteursanitaire

import com.transporteursanitaire.repository.UserRepository
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.transporteursanitaire.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.transporteursanitaire.ui.theme.TransporteurSanitaireTheme

/**
 * Activité principale de l'application "Transporteur Sanitaire".
 * Cette activité sert de conteneur pour les différents fragments (écrans)
 * et initialise l'utilisateur principal.
 */
class MainActivity : AppCompatActivity() {

    // Utilisation de ViewBinding pour un accès typé aux vues
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation du binding depuis le fichier layout "activity_main.xml"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optionnel : masquer l'ActionBar si souhaité
        // supportActionBar?.hide()

        // Initialisation de l'utilisateur principal (droit ADMIN)
//        UserRepository.initializePrincipalUser()

        // Vérifie si le NavHostFragment est bien présent - utilisé uniquement pour le débogage
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            Log.d("MainActivity", "NavHostFragment créé avec succès")
            Log.d("MainActivity", "startDestinationId = ${navController.graph.startDestinationId}")
        } else {
            Log.e("MainActivity", "NavHostFragment introuvable !")
        }

        // Ici, vous pourrez ensuite mettre en place la navigation entre les écrans (par ex. avec Navigation Component)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TransporteurSanitaireTheme {
        Greeting("Android")
    }
}