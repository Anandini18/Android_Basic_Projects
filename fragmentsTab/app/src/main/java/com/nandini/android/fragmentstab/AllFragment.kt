package com.nandini.android.fragmentstab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AllFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

     var recyclerView:RecyclerView? = null
     var dataList:ArrayList<String> = ArrayList()
     var adapterr:RecyclerView.Adapter<CustomAdapter.ViewHolder>?=null

    var galleryNumber: TextView?=null
    private final var MY_READ_PERMISSION_CODE=101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun loadImage(){
        recyclerView?.layoutManager=GridLayoutManager(context,3)
        dataList= ImageGallery().listOfImages(requireContext())
        adapterr= context?.let { CustomAdapter(it,dataList) }
        recyclerView?.adapter =adapterr
        galleryNumber?.text=("Photos ("+ dataList.size+")")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== MY_READ_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity,"Read external storage permission granted.",Toast.LENGTH_SHORT).show()
                loadImage()
            }else{
                Toast.makeText(activity,"Read externalstotrage permission denied.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryNumber=view?.findViewById(R.id.gallery_number)

        if(activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                requireActivity() ,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),MY_READ_PERMISSION_CODE)

        }else{
            loadImage()
        }

        var view:View=inflater.inflate(R.layout.fragment_all, container, false)
        recyclerView=view.findViewById(R.id.recyclerView)
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}