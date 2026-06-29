import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class LineNumberView extends JPanel implements DocumentListener {

    private JTextArea editor;

    public LineNumberView(JTextArea editor) {
        this.editor = editor;
        setBackground(Color.LIGHT_GRAY);
        editor.getDocument().addDocumentListener(this);
        atualizarTamanho();
    }

    private void atualizarTamanho() {
        FontMetrics fm = editor.getFontMetrics(editor.getFont());
        int fontHeight = fm.getHeight();
        int linhas = Math.max(editor.getLineCount(), 150); 
        int alturaTotal = linhas * fontHeight + editor.getInsets().top + editor.getInsets().bottom;
        
        setPreferredSize(new Dimension(40, alturaTotal));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setFont(editor.getFont());
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        int fontAscent = fm.getAscent();
        
        Rectangle clip = g.getClipBounds();
        int startOffset = editor.getInsets().top;

        int maxLines = Math.max(editor.getLineCount(), 150);

        for (int i = 1; i <= maxLines; i++) {
            int y = startOffset + (i - 1) * fontHeight + fontAscent;
            
            if (clip != null && y - fontAscent > clip.y + clip.height) {
                break; 
            }
            if (clip == null || y > clip.y) {
                String text = String.valueOf(i);
                int stringWidth = fm.stringWidth(text);
                g.drawString(text, getWidth() - stringWidth - 5, y);
            }
        }
    }

    @Override public void insertUpdate(DocumentEvent e) { atualizarTamanho(); }
    @Override public void removeUpdate(DocumentEvent e) { atualizarTamanho(); }
    @Override public void changedUpdate(DocumentEvent e) { atualizarTamanho(); }
}