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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.main.frontend.activities.driver.ReqAccepted;
import com.main.frontend.entity.AmbulanceOrder;

import java.util.List;

public class AmbRequestAdapter extends ArrayAdapter<AmbulanceOrder> {

    private Context mContext;
    private int resource;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    static class ViewHolder {
        TextView reqUserLocation;
        TextView reqUserCellphone;
        Button acceptReqBtn;
    }

    public AmbRequestAdapter(@NonNull Context context, int resource, @NonNull List<AmbulanceOrder> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.resource = resource;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String cellPhone = getItem(position).getUserid();
        String location = getItem(position).getLocation();
        String orderId = getItem(position).getOrderId();

        AmbulanceOrder order = new AmbulanceOrder();
        order.setUserid(cellPhone);
        order.setLocation(location);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.reqUserCellphone = convertView.findViewById(R.id.reqUserCell);
            viewHolder.reqUserLocation = convertView.findViewById(R.id.reqUserLocation);
            viewHolder.acceptReqBtn = convertView.findViewById(R.id.acceptReqButton);

            FirebaseUser user = auth.getCurrentUser();

            if (user != null) {
                viewHolder.acceptReqBtn.setOnClickListener(view -> {
                    DocumentReference reqRef = db.collection("ambulanceOrders").document(orderId);
                    reqRef.update("accepted", true, "driverId", user.getPhoneNumber())
                            .addOnSuccessListener(unused -> {
                                Log.d("ACCECPT", "doc updated");
                                Intent i = new Intent(view.getContext(), ReqAccepted.class);
                                i.putExtra("order", getItem(position));
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

        viewHolder.reqUserCellphone.setText(order.getUserid());
        viewHolder.reqUserLocation.setText(order.getLocation());

        return convertView;
    }
}
