package com.createsapp.kotlineatitv2server.ui.order

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.createsapp.kotlineatitv2server.R
import com.createsapp.kotlineatitv2server.SizeAddonEditActivity
import com.createsapp.kotlineatitv2server.adapter.MyOrderAdapter
import com.createsapp.kotlineatitv2server.callback.IMyButtonCallback
import com.createsapp.kotlineatitv2server.common.BottomSheetOrderFragment
import com.createsapp.kotlineatitv2server.common.Common
import com.createsapp.kotlineatitv2server.common.MySwipeHelper
import com.createsapp.kotlineatitv2server.eventbus.AddonSizeEditEvent
import com.createsapp.kotlineatitv2server.eventbus.ChangeMenuClick
import com.createsapp.kotlineatitv2server.eventbus.LoadOrderEvent
import com.createsapp.kotlineatitv2server.model.OrderModel
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class OrderFragment : Fragment() {

    lateinit var recycler_order: RecyclerView
    lateinit var layoutAnimationController: LayoutAnimationController

    lateinit var orderViewModel: OrderViewModel

    private var adapter: MyOrderAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_order, container, false)
        initViews(root)

        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderViewModel!!.messageError.observe(viewLifecycleOwner, Observer { s ->
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        })

        orderViewModel!!.getOrderModelList().observe(viewLifecycleOwner, Observer { orderList ->
            if (orderList != null) {
                adapter = MyOrderAdapter(requireContext(), orderList.toMutableList())
                recycler_order.adapter = adapter
                recycler_order.layoutAnimation = layoutAnimationController

                txt_order_filter.setText(
                    StringBuilder("Orders (")
                        .append(orderList.size)
                        .append(")")
                )
            }
        })

        return root
    }

    private fun initViews(root: View?) {

        setHasOptionsMenu(true)

        recycler_order = root!!.findViewById(R.id.recycler_order) as RecyclerView
        recycler_order.setHasFixedSize(true)
        recycler_order.layoutManager = LinearLayoutManager(context)

        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val swipe = object : MySwipeHelper(requireContext(), recycler_order!!, width / 6) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(context!!,
                        "Directions",
                        30,
                        0,
                        Color.parseColor("#9b0000"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {

                            }

                        })
                )

                buffer.add(
                    MyButton(context!!,
                        "Call",
                        30,
                        0,
                        Color.parseColor("#560027"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {

                                Dexter.withActivity(activity)
                                    .withPermission(android.Manifest.permission.CALL_PHONE)
                                    .withListener(object : PermissionListener {
                                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                                            val orderModel = adapter!!.getItemAtPosition(pos)
                                            val intent = Intent()
                                            intent.setAction(Intent.ACTION_DIAL)
                                            intent.setData(
                                                Uri.parse(
                                                    StringBuilder("tel: ")
                                                        .append(orderModel.userPhone).toString()
                                                )
                                            )
                                            startActivity(intent)

                                        }

                                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                            Toast.makeText(
                                                context,
                                                "You must accept this permission " + response!!.permissionName,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onPermissionRationaleShouldBeShown(
                                            permission: PermissionRequest?,
                                            token: PermissionToken?
                                        ) {

                                        }

                                    }).check()

                            }

                        })
                )

                buffer.add(
                    MyButton(context!!,
                        "Remove",
                        30,
                        0,
                        Color.parseColor("#12005e"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {

                                val orderModel = adapter!!.getItemAtPosition(pos)

                                val builder = AlertDialog.Builder(context!!)
                                    .setTitle("Delete")
                                    .setMessage("Do you really want to delete this order?")
                                    .setNegativeButton("CANCEL") { dialogInterface, i -> dialogInterface.dismiss() }
                                    .setPositiveButton("DELETE") { dialogInterface, i ->
                                        FirebaseDatabase.getInstance()
                                            .getReference(Common.ORDER_REF)
                                            .child(orderModel!!.key!!)
                                            .removeValue()
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    context!!,
                                                    "" + it.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }.addOnSuccessListener {
                                                adapter!!.removeItem(pos)
                                                adapter!!.notifyItemRemoved(pos)
                                                txt_order_filter.setText(
                                                    StringBuilder("Order (")
                                                        .append(adapter!!.itemCount)
                                                        .append(")")
                                                )

                                                dialogInterface.dismiss()
                                                Toast.makeText(context!!, "Order has been delete!" , Toast.LENGTH_SHORT).show()
                                            }
                                    }

                                val dialog = builder.create()
                                dialog.show()

                                val btnnegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                btnnegative.setTextColor(Color.LTGRAY)
                                val btnpositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                btnpositive.setTextColor(Color.RED)
                            }

                        })
                )

                buffer.add(
                    MyButton(context!!,
                        "Edit",
                        30,
                        0,
                        Color.parseColor("#333639"),
                        object : IMyButtonCallback {
                            override fun onClick(pos: Int) {


                            }

                        })
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.order_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            val bottomSheet = BottomSheetOrderFragment.instance
            bottomSheet!!.show(requireActivity().supportFragmentManager, "OrderList")
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(LoadOrderEvent::class.java))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent::class.java)
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)

        super.onStop()

    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(ChangeMenuClick(true))
        super.onDestroy()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadOrder(event: LoadOrderEvent) {
        orderViewModel.loadOrder(event.status)
    }

}