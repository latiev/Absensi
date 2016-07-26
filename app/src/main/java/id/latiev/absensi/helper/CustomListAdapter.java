package id.latiev.absensi.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.latiev.absensi.R;
import id.latiev.absensi.model.Kegiatan;

/**
 * Created by Latiev on 7/25/2016.
 */
public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.MyViewHolder>{

    private Context context;
    private List<Kegiatan> kegiatanList;

    public CustomListAdapter(Context context, List<Kegiatan> kegiatanList){
        this.context = context;
        this.kegiatanList = kegiatanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Kegiatan kegiatan = kegiatanList.get(position);
        holder.number.setText(kegiatan.getJam());
        holder.title.setText(kegiatan.getNamaKegiatan());
        holder.content.setText(kegiatan.getKeterangan());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(kegiatan.getNamaKegiatan(), kegiatan.getKeterangan());
            }
        });
    }

    @Override
    public int getItemCount() {
        return kegiatanList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, number;

        public MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.tv_cl_title);
            content = (TextView) view.findViewById(R.id.tv_cl_content);
            number = (TextView) view.findViewById(R.id.tv_cl_number);
        }
    }

    private void callDialog(String judul, String isi) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        TextView textViewTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        TextView textViewIsi = (TextView) view.findViewById(R.id.tv_dialog_isi);
        textViewIsi.setMovementMethod(new ScrollingMovementMethod());

        textViewTitle.setText(judul);
        textViewIsi.setText(isi);

        builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
