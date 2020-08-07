package com.rsruthiparvatha.covid19_app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyCustomAdapter extends ArrayAdapter<CountryModel> {

    private Context context;
    private List<CountryModel> countryModelList;
//  For filterd lists
    private List<CountryModel> countryModelListFiltered;

    public MyCustomAdapter(Context context, List<CountryModel> countryModelList) {
        super(context,R.layout.list_custom_item, countryModelList);
        this.context = context;
        this.countryModelList = countryModelList;
        //initialise the same countryModelList for countryModelListFiletered
        this.countryModelListFiltered = countryModelList;
    }



    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_item,null,true);
        TextView tvCountryName = view.findViewById(R.id.tvCountryName);
        ImageView imageFlag = view.findViewById(R.id.imageFlag);
// To set the country name in the ListView
        tvCountryName.setText(countryModelListFiltered.get(position).getCountry());
//        To set the image for the country flags in the ListView
        Picasso.with(context).load(countryModelListFiltered.get(position).getFlag()).into(imageFlag);
//        return super.getView(position, convertView, parent);
        return view;
    }

    //For searching countries
    //return no of items in countryModelListFiltered
    @Override
    public int getCount() {
        return countryModelListFiltered.size();
    }

    @Nullable
    @Override
    public CountryModel getItem(int position) {
        return countryModelListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //Most important function for searching
    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = countryModelList.size();
                    filterResults.values = countryModelList;

                }else{
                    List<CountryModel> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(CountryModel itemsModel:countryModelList){
                        if(itemsModel.getCountry().toLowerCase().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                countryModelListFiltered = (List<CountryModel>) results.values;
                AffectedCountries.countryModelList = (List<CountryModel>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }
}
