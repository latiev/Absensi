package id.latiev.absensi.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.latiev.absensi.R;
import id.latiev.absensi.model.Kegiatan;

/**
 * Created by Latiev on 7/20/2016.
 */
public class KegiatanTimelineAdapter extends RecyclerView.Adapter<KegiatanTimelineAdapter.MyViewHolder>{

    private Context context;
    private List<Kegiatan> kegiatanList;

    public KegiatanTimelineAdapter(Context context, List<Kegiatan> kegiatanList) {
        this.context = context;
        this.kegiatanList = kegiatanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Kegiatan kegiatan = kegiatanList.get(position);
        holder.textViewJam.setText(kegiatan.getJam());
        holder.textViewKegiatan.setText(kegiatan.getNamaKegiatan());
        holder.textViewKeterangan.setText(kegiatan.getKeterangan());
        holder.imageViewGarisAtas.setBackgroundResource(kegiatan.getGambarAtas());
        holder.imageViewGarisBawah.setBackgroundResource(kegiatan.getGambarBawah());

        /**
        if (kegiatan.getNamaKegiatan().equalsIgnoreCase("masuk")){
            holder.imageViewGarisAtas.setBackgroundResource(android.R.color.transparent);
        } else if (kegiatan.getNamaKegiatan().equalsIgnoreCase("pulang")){
            holder.imageViewGarisBawah.setBackgroundResource(android.R.color.transparent);
        }

        /**
        if (position == 0){
            holder.imageViewGarisAtas.setBackgroundResource(android.R.color.transparent);
        } else if (position == (kegiatanList.size() - 1)){
            holder.imageViewGarisBawah.setBackgroundResource(android.R.color.transparent);
        }
         */
    }

    @Override
    public int getItemCount() {
        return kegiatanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewJam, textViewKegiatan, textViewKeterangan;
        public ImageView imageViewGarisAtas, imageViewLingkaran, imageViewGarisBawah;

        public MyViewHolder(View view){
            super(view);
            textViewJam = (TextView) view.findViewById(R.id.tv_timeline_jam);
            textViewKegiatan = (TextView) view.findViewById(R.id.tv_timeline_kegiatan);
            textViewKeterangan = (TextView) view.findViewById(R.id.tv_timeline_keterangan);
            imageViewGarisAtas = (ImageView) view.findViewById(R.id.iv_timeline_garis_atas);
            imageViewLingkaran = (ImageView) view.findViewById(R.id.iv_timeline_lingkaran);
            imageViewGarisBawah = (ImageView) view.findViewById(R.id.iv_timeline_garis_bawah);
        }
    }
}
