package com.example.hype_up.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import com.example.hype_up.R
import com.example.hype_up.User
import com.example.hype_up.UserViewHolder
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PeopleFragment : Fragment() {

    companion object {
        private const val DELETED_VIEW_TYPE = 1
        private const val NORMAL_VIEW_TYPE = 2
    }
    lateinit var mAdapter : FirestorePagingAdapter<User, UserViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name",Query.Direction.ASCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        setupAdapter()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }


    private fun setupAdapter() {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(2)
            .build()
        val option = FirestorePagingOptions.Builder<User>()
            .setQuery(database, config, User::class.java)
            .setLifecycleOwner(this)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database, config, User::class.java)
            .build()

//        mAdapter = object :FirestorePagingAdapter<User, UserViewHolder>(option){
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//
//                val inflater = layoutInflater
//                return viewType {
//                    Companion.NORMAL_VIEW_TYPE -> {
//                        UserViewHolder(inflater.inflate(R.layout.list_item, parent, false))
//                    }
//                    else -> {
//                        EmptyViewHolder(inflater.inflate(R.layout.empty_view, parent, false))}
//                }
//                }
//
//            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
//
//
//                holder.bind(user = model)
//            }
//
//            override fun onLoadingStateChanged(state: LoadingState) {
//                super.onLoadingStateChanged(state)
//                when(state){
//                   LoadingState.LOADING_INITIAL -> {}
//                    LoadingState.LOADING_MORE -> {}
//                    LoadingState.LOADED -> {}
//                    LoadingState.FINISHED -> {}
//                    LoadingState.ERROR -> {
//                        Toast.makeText(
//                            requireContext(),
//                            "Error Occurred!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//
//            override fun onError(e: Exception) {
//                super.onError(e)
//                e.message?.let { Log.e("MainActivity", it) }
//            }
//
//
//        }
//    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        view.findViewById<RecyclerView>(R.id.userRv).apply {
//            adapter = mAdapter
//        }
//
//    }


    }
}

