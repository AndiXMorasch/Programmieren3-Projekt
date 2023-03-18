package hsos.prog3.projektarbeit.bitlocker.logik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hsos.prog3.projektarbeit.bitlocker.R;

/**
 * The RecyclerAdapter class associates the given data (website names) with the ViewHolder's views.
 * The RecyclerView requests those views and binds the views to their data, by calling methods in the RecyclerAdapter.
 *
 * @author Andreas Morasch
 */

// Sources:
// JavaDocs: https://developer.android.com/guide/topics/ui/layout/recyclerview
// RecyclerAdapter: https://www.youtube.com/watch?v=__OMnFR-wZU&ab_channel=BenO%27Brien
// https://www.youtube.com/watch?v=vBxNDtyE_Co&ab_channel=BenO%27Brien

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private final ArrayList<String> websiteList;
    private final RecyclerViewClickListener listener;

    /**
     * Constructor for the RecyclerAdapter class to initialize the dataset of the adapter.
     *
     * @param websiteList list of website names to be passed in order to create the recycler adapter
     * @param listener    click listener for the ViewHolder's view
     */

    public RecyclerAdapter(ArrayList<String> websiteList, RecyclerViewClickListener listener) {
        this.websiteList = websiteList;
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameTxt;

        /**
         * Constructor for the MyViewHolder class.
         *
         * @param view view parameter required
         */

        public MyViewHolder(final View view) {
            super(view);
            nameTxt = view.findViewById(R.id.websiteTextViewNameSpace);
            view.setOnClickListener(this);
        }

        /**
         * onClick listener to call the RecyclerViewClickListener's onClick method
         *
         * @param view view parameter required
         */

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    /**
     * RecyclerView calls this method whenever it needs to create a new ViewHolder.
     * The method creates and initializes the ViewHolder and its associated View, but
     * does not fill in the view's contents—the ViewHolder has not yet been bound to specific data.
     *
     * @param parent   view group required
     * @param viewType required view type
     * @return creates and initializes the ViewHolder and its associated View
     */

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     *
     * @param holder   RecyclerAdapter.MyViewHolder required
     * @param position position where the content will be replaced
     */

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String website = websiteList.get(position);
        holder.nameTxt.setText(website);
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     *
     * @return size of the dataset
     */

    @Override
    public int getItemCount() {
        return websiteList.size();
    }

    /**
     * RecyclerViewClickListener interface for the ViewHolder's View
     */

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
