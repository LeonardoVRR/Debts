package com.example.debts.layout_Item_lista

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemSpacingDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        // Define o espaçamento ao redor de cada item
        //outRect.left = spacing
        //outRect.right = spacing
        //outRect.top = spacing
        //outRect.bottom = spacing

        outRect.set(0, 0, 0, 0)  // Define o espaçamento superior, inferior, esquerdo e direito como 0
    }
}
