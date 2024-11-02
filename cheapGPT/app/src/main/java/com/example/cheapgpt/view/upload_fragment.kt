package com.example.cheapgpt.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.cheapgpt.databinding.FragmentUploadFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception
import java.util.UUID


class upload_fragment : Fragment() {

    private var _binding: FragmentUploadFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>

    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var storage:FirebaseStorage
    private lateinit var db:FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
        storage=Firebase.storage
        db=Firebase.firestore

        registerLaunchers()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener{butonSelect(it)}
        binding.imageView2.setOnClickListener { imageSelect(it) }
    }

    fun butonSelect(view: View){

        val uuid=UUID.randomUUID()
        val imageIdName="${uuid}.jpg"

        val reference=storage.reference
        val imageReference=reference.child("image").child(imageIdName)
        if (secilenGorsel != null){
            imageReference.putFile(secilenGorsel!!).addOnSuccessListener { uploadTask->

             imageReference.downloadUrl.addOnSuccessListener {uri->
                 if (auth.currentUser!=null){

                     val downloadUrl =uri.toString()
                     //Veritabanına Kayıt İşlemi
                     val postMap=HashMap<String,Any>()
                     postMap.put("downloadUrl",downloadUrl)
                     postMap.put("email",auth.currentUser!!.email.toString())
                     postMap.put("comment",binding.commentText.text.toString())
                     postMap.put("date",Timestamp.now())

                     db.collection("Posts").add(postMap).addOnSuccessListener {documentReference->
                         //veri databese gitti

                         val action=upload_fragmentDirections.actionUploadFragmentToRecylePage()
                         Navigation.findNavController(view).navigate(action)

                     }.addOnFailureListener { exception->
                         Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
                     }

                 }

             }


            }.addOnFailureListener{exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun imageSelect(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //read media image
           if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) !=PackageManager.PERMISSION_GRANTED){
                //izin yok
               if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                   //İZİN MANTIĞINI KULLANICIYA GÖSTERMEMİZ LAZIM
                   Snackbar.make(view,"Galeriye gitmek için izin vermeniz gerekiyor.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver"
                   ,View.OnClickListener {
                           permissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                       }).show()

               }else{
                   //İZİN İSTE
                   permissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
               }
           }
            else{
                //izin var
                //galeriye gitme kodu
               val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentToGallery)
           }

        }
        else{
            //READ EXTERNAL STORAGE

            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                //izin yok
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //İZİN MANTIĞINI KULLANICIYA GÖSTERMEMİZ LAZIM
                    Snackbar.make(view,"Galeriye gitmek için izin vermeniz gerekiyor.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver"
                        ,View.OnClickListener {
                            permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()

                }else{
                    //İZİN İSTE
                    permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else{
                //izin var
                //galeriye gitme kodu
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }


    fun registerLaunchers(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            if (result.resultCode ==RESULT_OK){
                val intenFromResult=result.data
                if (intenFromResult !=null){
                    secilenGorsel=intenFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >=28){
                            val source =ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            binding.imageView2.setImageBitmap(secilenBitmap)

                        }else{
                            secilenBitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageView2.setImageBitmap(secilenBitmap)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

        }


        permissionResultLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(requireContext(),"Galeriye Erişim Reddedildi",Toast.LENGTH_SHORT).show()
            }

        }

    }









    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}