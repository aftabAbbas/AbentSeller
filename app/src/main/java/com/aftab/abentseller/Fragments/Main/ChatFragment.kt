package com.aftab.abentseller.Fragments.Main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aftab.abentseller.Adapters.Recycler.RecentChatAdapter
import com.aftab.abentseller.Model.RecentChat
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.FragmentChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

@Suppress("deprecation")
class ChatFragment : Fragment() {


    private lateinit var binding: FragmentChatBinding
    private lateinit var mContext: Context

    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    private var userIdList = ArrayList<String>()
    private var recentChatList = ArrayList<RecentChat>()
    private var searchList = ArrayList<RecentChat>()

    var usersCount = 0
    var search = ""

    private lateinit var recentChatAdapter: RecentChatAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        initUI()
        clickListeners()
        getRecentChats()
        searchListeners()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    private fun searchListeners() {

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchList.clear()
                search =
                    s.toString().lowercase(
                        Locale.getDefault()
                    )
                recentChatAdapter = if (search != "") {
                    for (recentChat in recentChatList) {
                        val name = "${recentChat.users.fname} ${recentChat.users.lname}"
                        if (name.lowercase(Locale.getDefault())
                                .contains(search)
                        ) {
                            searchList.add(recentChat)
                        }
                    }

                    if (searchList.size > 0) {

                        binding.llNoData.visibility = View.GONE

                    }else{

                        binding.llNoData.visibility = View.VISIBLE

                    }

                    RecentChatAdapter(mContext, searchList)

                } else {

                    if (recentChatList.size > 0) {

                        binding.llNoData.visibility = View.GONE

                    }else{

                        binding.llNoData.visibility = View.VISIBLE

                    }

                    RecentChatAdapter(mContext, recentChatList)
                }

                binding.rvRecentChat.adapter = recentChatAdapter

            }

            override fun afterTextChanged(s: Editable) {}
        })


    }

    private fun initUI() {

        sh = SharedPref(requireContext())
        usersData = sh.getUsers()!!

    }

    private fun clickListeners() {

        binding.srlRecentChat.setOnRefreshListener(this::getRecentChats)

    }

    private fun getRecentChats() {

        binding.srlRecentChat.isRefreshing = true

        FireRef.CHATS
            .child(usersData.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    userIdList.clear()

                    for (dataSnapshot in snapshot.children) {

                        val userId = dataSnapshot.key.toString()

                        userIdList.add(userId)

                    }

                    usersCount = 0
                    recentChatList.clear()
                    getUserData()

                }

                override fun onCancelled(error: DatabaseError) {

                    binding.srlRecentChat.isRefreshing = false

                }
            })


    }

    private fun setAdapter() {

        if (recentChatList.size > 0) {

            binding.llNoData.visibility = View.GONE

        }else{

            binding.llNoData.visibility = View.VISIBLE

        }
        recentChatAdapter = RecentChatAdapter(this.mContext, recentChatList)
        binding.rvRecentChat.adapter = recentChatAdapter

    }

    fun getUserData() {

        if (usersCount < userIdList.size) {

            FireRef.USERS_REF
                .whereEqualTo(Constants.UID, userIdList[usersCount])
                .get()
                .addOnCompleteListener {

                    if (it.isSuccessful && it.result != null && it.result
                            .documents.size > 0
                    ) {

                        val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                        val users = documentSnapshot.toObject(Users::class.java)

                        if (users != null) {

                            val recentChat = RecentChat()
                            recentChat.userId = userIdList[usersCount]
                            recentChat.users = users

                            recentChatList.add(recentChat)

                        }


                    } else {

                        binding.srlRecentChat.isRefreshing = false

                        Toast.makeText(
                            requireContext(),
                            "FS Error " + it.exception?.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }

                    usersCount++
                    getUserData()

                }.addOnFailureListener {
                    binding.srlRecentChat.isRefreshing = false

                    Toast.makeText(requireContext(), "FS Error " + it.message, Toast.LENGTH_SHORT)
                        .show()

                }
        } else {

            binding.srlRecentChat.isRefreshing = false

            setAdapter()

        }

    }
}