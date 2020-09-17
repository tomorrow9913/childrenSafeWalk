package kr.co.woobi.tomorrow99.safewalk.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.woobi.tomorrow99.safewalk.R
import kr.co.woobi.tomorrow99.safewalk.databinding.LayoutTagBinding
import kr.co.woobi.tomorrow99.safewalk.model.Tag


/**
 * Created by SungBin on 2020-09-17.
 */

class TagAdapter(
    private val items: List<Tag>,
    private val activity: Activity
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    interface OnClickListener {
        fun onClick(item: Tag)
    }

    fun setOnClickListener(action: (Tag) -> Unit) {
        onClickListener = object : OnClickListener {
            override fun onClick(item: Tag) {
                action(item)
            }
        }
    }

    class ViewHolder(private val itemBinding: LayoutTagBinding, private val activity: Activity) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViewHolder(tag: Tag, listener: OnClickListener?) {
            with (itemBinding) {
                item = tag
                invalidateAll()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.layout_tag, viewGroup, false
            ), activity
        )

    override fun onBindViewHolder(@NonNull viewholder: ViewHolder, position: Int) {
        viewholder.bindViewHolder(items[position], onClickListener)
    }

    override fun getItemCount() = items.size
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
}