package com.aftab.abentseller.Activities.Main

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aftab.abentseller.Activities.Settings.AboutActivity
import com.aftab.abentseller.Activities.StartUp.LoginActivity
import com.aftab.abentseller.Fragments.Main.ChatFragment
import com.aftab.abentseller.Fragments.Main.EarningsFragment
import com.aftab.abentseller.Fragments.Main.HomeFragment
import com.aftab.abentseller.Fragments.Main.ProfileFragment
import com.aftab.abentseller.Model.Notifications
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Notifications.Helper.NotiHelper
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityHomeBinding
import com.aftab.abentseller.databinding.BlockedLayoutBinding
import com.aftab.abentseller.databinding.LogoutDialogBinding
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


@Suppress("deprecation")
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    private lateinit var animation: Animation

    private var unReadNotiList = ArrayList<Notifications>()
    var errorDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        setBottomNavigation()
        clickListeners()





    }

    private fun initUI() {

        animation = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top)

        sh = SharedPref(this)
        usersData = sh.getUsers()!!

        binding.llAdd.startAnimation(animation)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task: Task<InstanceIdResult?> ->
            if (task.isSuccessful && task.result != null) {
                val fcmToken = task.result!!.token
                val userInfo = HashMap<String, Any>()
                userInfo[Constants.FCM] = fcmToken

                FireRef.USERS_REF.document(usersData.uid)
                    .update(userInfo)
                    .addOnCompleteListener {


                    }.addOnFailureListener {


                    }

            }
        }

    }


    private fun clickListeners() {

        binding.llHome.setOnClickListener {

            setFragment(HomeFragment())
            setFragmentViews(0)

        }

        binding.llEarnings.setOnClickListener {

            setFragment(EarningsFragment())
            setFragmentViews(1)

        }

        binding.llChat.setOnClickListener {

            setFragment(ChatFragment())
            setFragmentViews(2)

        }

        binding.llProfile.setOnClickListener {

            setFragment(ProfileFragment())
            setFragmentViews(3)

        }

        binding.ivNotification.setOnClickListener {

            NotiHelper.sendNotification(
                this,
                "APA91bEnlJBZZfQkk31zoC83INBvN8K2kkA8Vj58vY6ZaxBB5wbL4GZfPnLDJKGtrgPeDKpHxBkDo9zkGIkAtAazNN0gTDpy6maoHjXz-0QOKjZWpp8R3NxutDS3O0avZwmwekqg6-Ga",
                resources.getString(R.string.order_picked),
                "msg",
                Constants.REMOTE_MSG_ORDER,
                "customerData.uid",
                usersData.uid
            )
           // startActivity(Intent(this, NotificationsActivity::class.java))

        }


        binding.llAdd.setOnClickListener {

            startActivity(Intent(this, SelectProductCategoryActivity::class.java))

        }

        binding.ivQrCode.setOnClickListener {

            startActivity(Intent(this, ScanQRCodeActivity::class.java))

        }
    }

    private fun setBottomNavigation() {

        setFragment(HomeFragment())
        setFragmentViews(0)


    }

    private fun setFragmentViews(i: Int) {

        when (i) {
            0 -> {

                binding.tvHome.visibility = View.VISIBLE
                binding.tvEarnings.visibility = View.GONE
                binding.tvChat.visibility = View.GONE
                binding.tvProfile.visibility = View.GONE

                binding.ivHome.setColorFilter(R.color.purple_500)
                binding.ivEarnings.setColorFilter(R.color.black)
                binding.ivChat.setColorFilter(R.color.black)
                binding.ivProfile.setColorFilter(R.color.black)

                Functions.setMargins(binding.ivHome, 0, 0, 0, 0)
                Functions.setMargins(binding.ivEarnings, 0, 0, 0, 25)
                Functions.setMargins(binding.ivChat, 0, 0, 0, 25)
                Functions.setMargins(binding.ivProfile, 0, 0, 0, 25)

                binding.toolbar.title = "Home"
                binding.toolbar.visibility = View.VISIBLE
                binding.llButtons.visibility = View.VISIBLE
                binding.tvProfileText.visibility = View.INVISIBLE

            }
            1 -> {

                binding.tvHome.visibility = View.GONE
                binding.tvEarnings.visibility = View.VISIBLE
                binding.tvChat.visibility = View.GONE
                binding.tvProfile.visibility = View.GONE

                binding.ivHome.setColorFilter(R.color.black)
                binding.ivEarnings.setColorFilter(R.color.purple_500)
                binding.ivChat.setColorFilter(R.color.black)
                binding.ivProfile.setColorFilter(R.color.black)


                Functions.setMargins(binding.ivHome, 0, 0, 0, 25)
                Functions.setMargins(binding.ivEarnings, 0, 0, 0, 0)
                Functions.setMargins(binding.ivChat, 0, 0, 0, 25)
                Functions.setMargins(binding.ivProfile, 0, 0, 0, 25)

                binding.toolbar.title = "Earning Stats"
                binding.toolbar.visibility = View.VISIBLE
                binding.llButtons.visibility = View.INVISIBLE
                binding.tvProfileText.visibility = View.INVISIBLE

            }
            2 -> {

                binding.tvHome.visibility = View.GONE
                binding.tvEarnings.visibility = View.GONE
                binding.tvChat.visibility = View.VISIBLE
                binding.tvProfile.visibility = View.GONE

                binding.ivHome.setColorFilter(R.color.black)
                binding.ivEarnings.setColorFilter(R.color.black)
                binding.ivChat.setColorFilter(R.color.purple_500)
                binding.ivProfile.setColorFilter(R.color.black)


                Functions.setMargins(binding.ivHome, 0, 0, 0, 25)
                Functions.setMargins(binding.ivEarnings, 0, 0, 0, 25)
                Functions.setMargins(binding.ivChat, 0, 0, 0, 0)
                Functions.setMargins(binding.ivProfile, 0, 0, 0, 25)

                binding.toolbar.title = "Chat"
                binding.toolbar.visibility = View.VISIBLE
                binding.llButtons.visibility = View.INVISIBLE
                binding.tvProfileText.visibility = View.INVISIBLE

            }
            3 -> {

                binding.tvHome.visibility = View.GONE
                binding.tvEarnings.visibility = View.GONE
                binding.tvChat.visibility = View.GONE
                binding.tvProfile.visibility = View.VISIBLE

                binding.ivHome.setColorFilter(R.color.black)
                binding.ivEarnings.setColorFilter(R.color.black)
                binding.ivChat.setColorFilter(R.color.black)
                binding.ivProfile.setColorFilter(R.color.purple_500)


                Functions.setMargins(binding.ivHome, 0, 0, 0, 25)
                Functions.setMargins(binding.ivEarnings, 0, 0, 0, 25)
                Functions.setMargins(binding.ivChat, 0, 0, 0, 25)
                Functions.setMargins(binding.ivProfile, 0, 0, 0, 0)

                binding.toolbar.visibility = View.INVISIBLE
                binding.tvProfileText.visibility = View.VISIBLE

            }
        }


    }

    private fun setFragment(fragment: Fragment) {

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_home, fragment)
        transaction.commit()


    }

    override fun onResume() {
        super.onResume()
        navigationSet()
        headerSetting()
        Functions.setStatusBarTransparent(this)
        getUnReadNotifications()
        checkAccountAvailability()
    }


    private fun getUnReadNotifications() {

        FireRef.NOTIFICATIONS_REF
            .whereEqualTo(Constants.RECEIVER_ID, usersData.uid)
            .whereEqualTo(Constants.READ, false)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    unReadNotiList.clear()

                    for (document in it.result) {

                        val notifications: Notifications =
                            document.toObject(Notifications::class.java)

                        unReadNotiList.add(notifications)

                    }

                    if (unReadNotiList.size > 0) {

                        binding.cvNotificationsCount.visibility = View.VISIBLE

                    } else {

                        binding.cvNotificationsCount.visibility = View.INVISIBLE

                    }

                } else {

                    binding.cvNotificationsCount.visibility = View.INVISIBLE

                }


            }

    }

    private fun navigationSet() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
        toggle.syncState()
        binding.navigationView.itemIconTintList = null

        binding.navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_home -> {

                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    binding.llHome.performClick()

                }
                R.id.menu_about -> {

                    startActivity(Intent(this, AboutActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.menu_logout -> {

                    openLogoutDialog()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)

                }
            }
            false
        }
    }

    private fun openLogoutDialog() {

        val dialog = Dialog(this)
        val binding = LogoutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        binding.btnClose.setOnClickListener {

            dialog.dismiss()

        }

        binding.btnLogout.setOnClickListener {

            logout()

        }


    }

    private fun logout() {

        FireRef.mAuth.signOut()
        sh.clearPreferences()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }

    private fun headerSetting() {
        val headerView: View = binding.navigationView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tv_name)
        val tvEmail = headerView.findViewById<TextView>(R.id.tv_email)
        val cvUser: CircleImageView = headerView.findViewById(R.id.civ_user)
        var fName = usersData.fName
        var lName = usersData.lName
        fName = fName.substring(0, 1).toUpperCase(Locale.ROOT) + fName.substring(1).toLowerCase(
            Locale.ROOT
        )
        lName = lName.substring(0, 1).toUpperCase(Locale.ROOT) + lName.substring(1).toLowerCase(
            Locale.ROOT
        )
        val name = "$fName $lName"
        val email = usersData.email
        tvName.text = name
        tvEmail.text = email

        Glide.with(this)
            .load(usersData.dp).placeholder(R.drawable.place_holder).into(cvUser)
    }

    private fun checkAccountAvailability() {

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, usersData.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    val users = documentSnapshot.toObject(Users::class.java)

                    if (users != null) {

                        if (users.userStatus == Constants.BLOCKED) {

                            openBlockedDialog()

                        }

                    }


                }

            }

    }

    private fun openBlockedDialog() {

        val dialog = Dialog(this)
        val binding = BlockedLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        binding.btnClose.setOnClickListener {

            finish()

        }

        binding.btnContactUs.setOnClickListener {


            Functions.composeEmail(this, "abc@gmail.com", "")

        }


    }


}