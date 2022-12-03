package com.main.frontend.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.driver.ReqAccepted;
import com.main.frontend.activities.hospital.HospitalHomepage;
import com.main.frontend.activities.user.BedReqAccepted;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.BedOrder;

import java.util.List;

public class BedRequestAdapter extends ArrayAdapter<BedOrder> {

    FirebaseAuth auth;
    FirebaseFirestore db;

    Context mContext;
    int resource;

    static class ViewHolder {
        TextView ageTV;
        TextView specTV;
        Button acceptReqBtn;
    }

    public BedRequestAdapter(@NonNull Context context, int resource, @NonNull List<BedOrder> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.resource = resource;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }


    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int age = getItem(position).getAge();
        String orderId = getItem(position).getOrderId();

        BedOrder bedOrder = new BedOrder();
        bedOrder.setOrderId(orderId);
        bedOrder.setAge(age);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ageTV = convertView.findViewById(R.id.reqUserCell);
            viewHolder.specTV = convertView.findViewById(R.id.reqUserLocation);
            viewHolder.acceptReqBtn = convertView.findViewById(R.id.acceptReqButton);

            FirebaseUser user = auth.getCurrentUser();

            if (user != null) {
                viewHolder.acceptReqBtn.setOnClickListener(view -> {
                    DocumentReference reqRef = db.collection("bedOrders").document(orderId);
                    reqRef.update("accepted", true, "hospId", user.getPhoneNumber())
                            .addOnSuccessListener(unused -> {
                                Log.d("ACCEPT", "doc updated");
                                Intent i = new Intent(view.getContext(), HospitalHomepage.class);
                                Toast.makeText(mContext, "Request accepted", Toast.LENGTH_SHORT).show();
                                mContext.startActivity(i);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("ACCEPT ORDER", e);
                            });
                });
            }
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }


}
