package com.tonyakitori.biometrictests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.tonyakitori.biometrictests.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var canAuthenticateWithBiometrics = false
    private lateinit var promptInfo: PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        biometricSetup()

        return binding.root
    }

    private fun biometricSetup() {
        if (BiometricManager.from(requireContext())
                .canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            canAuthenticateWithBiometrics = true
            promptInfo = PromptInfo.Builder()
                .setTitle("Epa que adivino si eres mi dueño")
                .setDescription("Tu carita o dedito please")
                .setAllowedAuthenticators(
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
                .build()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            authenticateWithBiometrics{
                if(it){
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }else{
                    Toast.makeText(requireContext(), "No se puede carnal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun authenticateWithBiometrics(auth: (auth: Boolean) -> Unit){
        if(canAuthenticateWithBiometrics){
            BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object: AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    auth(true)
                }
            }).authenticate(promptInfo)
        }else{
            auth(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}