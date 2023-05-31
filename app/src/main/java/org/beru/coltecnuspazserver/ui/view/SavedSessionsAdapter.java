package org.beru.coltecnuspazserver.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.ui.model.ConnectorSSH;
import org.beru.coltecnuspazserver.ui.model.DataPersistence;
import org.beru.coltecnuspazserver.ui.model.Datas;
import org.beru.coltecnuspazserver.ui.model.Encrypter;
import org.beru.coltecnuspazserver.ui.model.SessionDatas;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<SessionDatas> datas = new ArrayList<>();
    public SavedSessionsAdapter(Context context, List<SessionDatas> datas){
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_saved_sessions, null);
        TextView name = view.findViewById(R.id.savedName);
        TextView host = view.findViewById(R.id.saved_host);
        TextView port = view.findViewById(R.id.saved_port);
        TextView pass = view.findViewById(R.id.saved_pass);
        name.setText(datas.get(i).getName());
        host.setText(datas.get(i).getHost());
        port.setText(""+datas.get(i).getPort());
        pass.setText(datas.get(i).getPass());
        return view;
    }
}
