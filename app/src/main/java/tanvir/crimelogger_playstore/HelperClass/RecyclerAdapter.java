package tanvir.crimelogger_playstore.HelperClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;



;import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tanvir.crimelogger_playstore.ModelClass.UserPostMC;
import tanvir.crimelogger_playstore.R;

/**
 * Created by USER on 01-Feb-17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {


    ArrayList<UserPostMC> userPostMCS;
    Context context;
    private String cameFromWhichActivity="";

    Glide glide;

    public RecyclerAdapter(Context context, ArrayList<UserPostMC> userPostMCS, String cameFromWhichActivity) {
        this.context = context;
        this.userPostMCS = userPostMCS;
        this.cameFromWhichActivity=cameFromWhichActivity;



    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layou_to_inflate_in_recyclerview, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, userPostMCS);
        return recyclerViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        String userName = userPostMCS.get(position).getUserName();

        holder.crimePlace.setText(userPostMCS.get(position).getCrimePlace());
        holder.crimeDate.setText(userPostMCS.get(position).getCrimeDate());
        holder.crimeTime.setText(userPostMCS.get(position).getCrimeTime());
        holder.crimeType.setText(userPostMCS.get(position).getCrimeType());
        holder.crimeDesc.setText(userPostMCS.get(position).getCrimeDesc());
        holder.userName.setText(userPostMCS.get(position).getUserName());

        String postDateAndTime = userPostMCS.get(position).getPostDateAndTime();



        int divideposition  = seperateDateAndTime(postDateAndTime);
        holder.postDate.setText(postDateAndTime.substring(0,divideposition));
        holder.postTime.setText(postDateAndTime.substring(divideposition+1));

        if (position==0)
            ////Toast.makeText(context, "position : "+Integer.toString(position)+"UserName : "+userName, Toast.LENGTH_SHORT).show();

        userName=userName.trim();
        if (!userPostMCS.get(position).getUserName().equals("Anonymous"))
        {

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.signature(new ObjectKey(System.currentTimeMillis()));
            options.placeholder(R.drawable.person2);


            String url="http://www.farhandroid.com/CrimeLogger/Script/UserProfilePic/"+userName+".jpg";

            glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(holder.userImage);
        }
        else
        {


            Glide.with(context)
                    .load(context.getDrawable(R.drawable.person2))
                    .into(holder.userImage);


        }


    }

    @Override
    public int getItemCount() {
        return userPostMCS.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder

    {
        TextView userName, crimePlace, crimeDate, crimeTime, crimeType, crimeDesc, postDate, postTime;
        CardView cardView;

        ArrayList<UserPostMC> userPostMCS;

        CircleImageView userImage;

        LinearLayout linearLayout;


        Context context;


        public RecyclerViewHolder(View view, final Context context, final ArrayList<UserPostMC> userPostMCS) {
            super(view);

            this.context = context;
            this.userPostMCS = userPostMCS;

           // Toast.makeText(context, "Context : "+ context.toString(), Toast.LENGTH_SHORT).show();

            userName = view.findViewById(R.id.postedByTV);
            crimePlace = view.findViewById(R.id.crimePlaceTV);
            crimeDate = view.findViewById(R.id.crimeDateTV);
            crimeTime = view.findViewById(R.id.crimeTimeTV);
            crimeType = view.findViewById(R.id.crimeTypeTV);
            crimeDesc = view.findViewById(R.id.crimeDescTV);
            postDate = view.findViewById(R.id.postDateTV);
            postTime = view.findViewById(R.id.postTimeTV);
            userImage=view.findViewById(R.id.imageViewInRecyclerView);

            cardView=view.findViewById(R.id.cardViewRV);

            linearLayout = view.findViewById(R.id.recylerViewLL);

            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    int position = getAdapterPosition();


                   /*Activity activity = (Activity) context;
                    Intent myIntent = new Intent(context, PostViewActivity.class);
                    myIntent.putExtra("cameFromWhichActivity",cameFromWhichActivity);
                    myIntent.putExtra("postDateAndTime", userPostMCS.get(position).getPostDateAndTime());
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    activity.startActivity(myIntent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);*/


                    ///Toast.makeText(context, "Click on : "+userPostMCS.get(position).getPostDateAndTime(), Toast.LENGTH_SHORT).show();


                }
            });




        }
    }

    public int seperateDateAndTime(String postDateAndTime)
    {
        int p = postDateAndTime.indexOf(" ");

       return p;
    }
}