package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.R;

import java.util.List;

public class ListOfListsAdapter extends ArrayAdapter<Game> {
    Context cont;
    int res;
    List<Game> gameList;

    public ListOfListsAdapter(Context cnt, int resource, List<Game> list) {
        super(cnt, resource, list);

        cont = cnt;
        res = resource;
        gameList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(cont);
        View view = inf.inflate(res, null);

        return view;
    }
}
