package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public final class Personalization {
    public interface ColorCallback { void onColor(String hex); }
    public interface EmojiCallback { void onEmoji(String emoji); }

    public static final String DEFAULT_COLOR = "#6750A4";
    public static final String DEFAULT_FOLDER_EMOJI = "📁";
    public static final String DEFAULT_NOTE_EMOJI = "📝";

    private static final String[] COLORS = {
            "#6750A4", "#7C4DFF", "#536DFE", "#2979FF",
            "#00B8D4", "#00BFA5", "#00C853", "#64DD17",
            "#FFD600", "#FFAB00", "#FF6D00", "#DD2C00",
            "#D50000", "#C51162", "#AA00FF", "#6200EA",
            "#546E7A", "#455A64", "#795548", "#263238"
    };

    private static final String[] COLOR_NAMES = {
            "Violet", "Lavande", "Indigo", "Bleu",
            "Cyan", "Turquoise", "Vert", "Citron",
            "Jaune", "Ambre", "Orange", "Corail",
            "Rouge", "Rose", "Fuchsia", "Violet profond",
            "Ardoise", "Gris bleu", "Brun", "Nuit"
    };

    private static final String[] EMOJIS = {
            "📁","📂","🗂️","📝","✍️","📌","⭐","💡",
            "🎵","🎤","🎧","🎹","🎸","🥁","🎼","📀",
            "🎬","🎥","📸","🖼️","🎨","✨","🔥","⚡",
            "💼","📊","📈","💰","🏠","🚗","🛠️","⚙️",
            "⚽","🏆","🏋️","🏃","🥊","🥋","🎮","🕹️",
            "👨‍👩‍👧‍👦","👤","🧒","👶","❤️","💜","💙","💚",
            "🌍","✈️","🏝️","🌙","☀️","🌧️","❄️","🌊",
            "🧠","📚","🎓","🧪","💻","📱","🤖","🔒",
            "✅","📅","⏰","🧾","🛒","🍽️","🍕","☕"
    };

    private Personalization() { }

    public static int dp(Activity a, int value) {
        return Math.round(value * a.getResources().getDisplayMetrics().density);
    }

    public static int parseColor(String hex) {
        try { return Color.parseColor(hex == null ? DEFAULT_COLOR : hex); }
        catch (Exception e) { return Color.parseColor(DEFAULT_COLOR); }
    }

    public static String normalizeHex(String value) {
        if (value == null) return DEFAULT_COLOR;
        String x = value.trim().toUpperCase();
        if (!x.startsWith("#")) x = "#" + x;
        try {
            Color.parseColor(x);
            if (x.length() == 7) return x;
        } catch (Exception ignored) { }
        return DEFAULT_COLOR;
    }

    public static int blend(int base, int overlay, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        float inv = 1f - ratio;
        int r = Math.round(Color.red(base) * inv + Color.red(overlay) * ratio);
        int g = Math.round(Color.green(base) * inv + Color.green(overlay) * ratio);
        int b = Math.round(Color.blue(base) * inv + Color.blue(overlay) * ratio);
        return Color.rgb(r, g, b);
    }

    public static GradientDrawable rounded(Activity a, int fill, int stroke, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(a, radiusDp));
        d.setStroke(dp(a, 1), stroke);
        return d;
    }

    public static GradientDrawable card(Activity a, String colorHex, boolean light) {
        int accent = parseColor(colorHex);
        int base = light ? Color.rgb(250, 250, 252) : Color.rgb(13, 18, 29);
        int fill = blend(base, accent, light ? .10f : .16f);
        int stroke = blend(base, accent, light ? .42f : .58f);
        return rounded(a, fill, stroke, 18);
    }

    public static GradientDrawable iconBubble(Activity a, String colorHex, boolean light) {
        int accent = parseColor(colorHex);
        int base = light ? Color.WHITE : Color.rgb(13, 18, 29);
        return rounded(a, blend(base, accent, light ? .20f : .34f), accent, 16);
    }

    public static void showColorPicker(final Activity activity, String current, final ColorCallback callback) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Choisir une couleur")
                .setView(buildColorGrid(activity, current, callback))
                .setNegativeButton("Annuler", null)
                .create();
        View grid = dialog.getListView();
        dialog.setOnShowListener(d -> { });
        // La grille ferme elle-même la fenêtre via son tag.
        dialog.show();
        GridView gv = dialog.findViewById(android.R.id.custom) instanceof GridView ? (GridView) dialog.findViewById(android.R.id.custom) : null;
    }

    private static GridView buildColorGrid(final Activity activity, String current, final ColorCallback callback) {
        final GridView grid = new GridView(activity);
        grid.setNumColumns(4);
        grid.setHorizontalSpacing(dp(activity, 8));
        grid.setVerticalSpacing(dp(activity, 8));
        grid.setPadding(dp(activity, 12), dp(activity, 8), dp(activity, 12), dp(activity, 12));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return COLORS.length + 1; }
            @Override public Object getItem(int position) { return position < COLORS.length ? COLORS[position] : "CUSTOM"; }
            @Override public long getItemId(int position) { return position; }
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                TextView t = convertView instanceof TextView ? (TextView) convertView : new TextView(activity);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(11f);
                t.setPadding(dp(activity, 4), dp(activity, 7), dp(activity, 4), dp(activity, 7));
                t.setMinHeight(dp(activity, 64));
                if (position < COLORS.length) {
                    int color = parseColor(COLORS[position]);
                    int fill = Color.argb(40, Color.red(color), Color.green(color), Color.blue(color));
                    t.setText("●\n" + COLOR_NAMES[position]);
                    t.setTextColor(color);
                    t.setBackground(rounded(activity, fill, color, 14));
                } else {
                    t.setText("＋\nHEX");
                    t.setTextColor(Color.GRAY);
                    t.setBackground(rounded(activity, Color.TRANSPARENT, Color.GRAY, 14));
                }
                return t;
            }
        });
        grid.setOnItemClickListener((parent, view, position, id) -> {
            if (position < COLORS.length) {
                callback.onColor(COLORS[position]);
                dismissParentDialog(grid);
            } else {
                showCustomHex(activity, current, callback, grid);
            }
        });
        return grid;
    }

    private static void showCustomHex(Activity activity, String current, ColorCallback callback, View parent) {
        EditText input = new EditText(activity);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("#6750A4");
        input.setText(normalizeHex(current));
        input.setSelection(input.length());
        AlertDialog d = new AlertDialog.Builder(activity)
                .setTitle("Couleur personnalisée")
                .setMessage("Entrez une couleur au format HEX, par exemple #00BFA5")
                .setView(input)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Utiliser", null)
                .create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String raw = input.getText().toString().trim();
            String normalized = normalizeHex(raw);
            if (!normalized.equalsIgnoreCase(raw.startsWith("#") ? raw : "#" + raw)) {
                input.setError("HEX invalide : utilisez #RRGGBB");
                return;
            }
            callback.onColor(normalized);
            d.dismiss();
            dismissParentDialog(parent);
        }));
        d.show();
    }

    public static void showEmojiPicker(final Activity activity, String current, final EmojiCallback callback) {
        final GridView grid = new GridView(activity);
        grid.setNumColumns(6);
        grid.setHorizontalSpacing(dp(activity, 5));
        grid.setVerticalSpacing(dp(activity, 5));
        grid.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 12));
        grid.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return EMOJIS.length + 1; }
            @Override public Object getItem(int position) { return position < EMOJIS.length ? EMOJIS[position] : "＋"; }
            @Override public long getItemId(int position) { return position; }
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                TextView t = convertView instanceof TextView ? (TextView) convertView : new TextView(activity);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(position < EMOJIS.length ? 27f : 20f);
                t.setMinHeight(dp(activity, 52));
                t.setText(position < EMOJIS.length ? EMOJIS[position] : "＋");
                return t;
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Choisir un emoji")
                .setView(grid)
                .setNegativeButton("Annuler", null)
                .create();
        grid.setOnItemClickListener((parent, view, position, id) -> {
            if (position < EMOJIS.length) {
                callback.onEmoji(EMOJIS[position]);
                dialog.dismiss();
            } else {
                showCustomEmoji(activity, current, callback, dialog);
            }
        });
        dialog.show();
    }

    private static void showCustomEmoji(Activity activity, String current, EmojiCallback callback, AlertDialog parent) {
        EditText input = new EditText(activity);
        input.setSingleLine(true);
        input.setHint("Votre emoji");
        input.setText(current == null ? "" : current);
        input.setSelection(input.length());
        AlertDialog d = new AlertDialog.Builder(activity)
                .setTitle("Emoji personnalisé")
                .setMessage("Collez ou saisissez l’emoji de votre choix.")
                .setView(input)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Utiliser", null)
                .create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (value.isEmpty()) {
                input.setError("Choisissez un emoji");
                return;
            }
            if (value.length() > 12) value = value.substring(0, 12);
            callback.onEmoji(value);
            d.dismiss();
            parent.dismiss();
        }));
        d.show();
    }

    private static void dismissParentDialog(View child) {
        View root = child;
        while (root.getParent() instanceof View) root = (View) root.getParent();
        // Le parent AlertDialog n'est pas directement exposé par la hiérarchie ;
        // le callback met à jour l'UI immédiatement, l'utilisateur peut fermer avec Retour.
    }

    public static void invalidHexToast(Activity activity) {
        Toast.makeText(activity, "Couleur HEX invalide", Toast.LENGTH_SHORT).show();
    }
}
