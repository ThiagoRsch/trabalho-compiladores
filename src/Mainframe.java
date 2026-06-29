import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class Mainframe extends JFrame {

    JTextArea editor;
    JTextArea mensagens;
    JLabel status;
    String caminhoArquivo = null;

    public Mainframe() {
        setTitle("Compilador - Interface");
        setSize(1500, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        toolbar.setPreferredSize(new Dimension(1500, 70));
        toolbar.setFloatable(false);

        Dimension tamanhoBotao = new Dimension(140, 60);

        JButton btnNovo = criarBotao("novo [ctrl-n]", new IconeGerado(new Color(46, 204, 113), "📄"), tamanhoBotao);
        JButton btnAbrir = criarBotao("abrir [ctrl-o]", new IconeGerado(new Color(241, 196, 15), "📂"), tamanhoBotao);
        JButton btnSalvar = criarBotao("salvar [ctrl-s]", new IconeGerado(new Color(52, 152, 219), "💾"), tamanhoBotao);

        JButton btnCopiar = criarBotao("copiar [ctrl-c]", new IconeGerado(new Color(155, 89, 182), "⧉"), tamanhoBotao);
        JButton btnColar = criarBotao("colar [ctrl-v]", new IconeGerado(new Color(230, 126, 34), "📋"), tamanhoBotao);
        JButton btnRecortar = criarBotao("recortar [ctrl-x]", new IconeGerado(new Color(231, 76, 60), "✂"), tamanhoBotao);

        JButton btnCompilar = criarBotao("compilar [F7]", new IconeGerado(new Color(52, 73, 94), "⚙"), tamanhoBotao);
        JButton btnEquipe = criarBotao("equipe [F1]", new IconeGerado(new Color(149, 165, 166), "👥"), tamanhoBotao);

        toolbar.add(btnNovo);
        toolbar.add(btnAbrir);
        toolbar.add(btnSalvar);
        toolbar.addSeparator();
        toolbar.add(btnCopiar);
        toolbar.add(btnColar);
        toolbar.add(btnRecortar);
        toolbar.addSeparator();
        toolbar.add(btnCompilar);
        toolbar.add(btnEquipe);

        add(toolbar, BorderLayout.NORTH);

        editor = new JTextArea();
        JScrollPane scrollEditor = new JScrollPane(editor);

        scrollEditor.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollEditor.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollEditor.getVerticalScrollBar().setUI(new BarraSutil());
        scrollEditor.getHorizontalScrollBar().setUI(new BarraSutil());

        LineNumberView linhas = new LineNumberView(editor);
        scrollEditor.setRowHeaderView(linhas);

        mensagens = new JTextArea();
        mensagens.setEditable(false);
        JScrollPane scrollMsg = new JScrollPane(mensagens);

        scrollMsg.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMsg.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollMsg.getVerticalScrollBar().setUI(new BarraSutil());
        scrollMsg.getHorizontalScrollBar().setUI(new BarraSutil());

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollEditor, scrollMsg);
        split.setDividerLocation(550);
        add(split, BorderLayout.CENTER);

        status = new JLabel("pasta/nome do arquivo");

        JPanel barraStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraStatus.setPreferredSize(new Dimension(1500, 25));
        barraStatus.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        barraStatus.add(status);

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(barraStatus, BorderLayout.SOUTH);
        add(painelInferior, BorderLayout.SOUTH);

        configurarAcoes(btnNovo, btnAbrir, btnSalvar, btnCopiar, btnColar, btnRecortar, btnCompilar, btnEquipe);

        setVisible(true);
    }

    private JButton criarBotao(String texto, Icon icone, Dimension tamanho) {
        JButton btn = new JButton(texto);
        btn.setIcon(icone);
        btn.setPreferredSize(tamanho);
        btn.setMaximumSize(tamanho);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        return btn;
    }

    private String obterClasseToken(int idToken) {
        if (idToken == Constants.t_identificador) {
            return "identificador";
        }
        if (idToken == Constants.t_constante_int) {
            return "constante_int";
        }
        if (idToken == Constants.t_constante_float) {
            return "constante_float";
        }
        if (idToken == Constants.t_constante_char) {
            return "constante_char";
        }
        if (idToken == Constants.t_constante_string) {
            return "constante_string";
        }
        if (idToken >= Constants.t_pr_ask && idToken <= Constants.t_pr_while) {
            return "palavra reservada";
        }
        return "símbolo especial";
    }


    private int calcularLinha(String texto, int pos) {
        int linha = 1;
        if (pos < 0) {
            return linha;
        }
        int limite = Math.min(pos, texto.length());
        for (int i = 0; i < limite; i++) {
            if (texto.charAt(i) == '\n') {
                linha++;
            }
        }
        return linha;
    }

    private Token obterTokenNaPosicao(String codigoFonte, int pos) throws LexicalError {
        Lexico lexico = new Lexico(codigoFonte);
        Token token;
        while ((token = lexico.nextToken()) != null) {
            if (token.getPosition() == pos) {
                return token;
            }
            if (token.getPosition() > pos) {
                return token;
            }
        }
        return new Token(Constants.DOLLAR, "$", codigoFonte.length());
    }

    private String formatarTokenEncontrado(Token token) {
        if (token == null || token.getId() == Constants.DOLLAR || "$".equals(token.getLexeme())) {
            return "EOF";
        }
        if (token.getId() == Constants.t_constante_string) {
            return "constante_string";
        }
        return token.getLexeme();
    }


    private void gerarArquivoIL(String codigoObjeto) throws IOException {
        if (caminhoArquivo == null) {
            return;
        }

        File arquivoFonte = new File(caminhoArquivo);
        String nome = arquivoFonte.getName();
        int ponto = nome.lastIndexOf('.');
        String nomeBase = ponto >= 0 ? nome.substring(0, ponto) : nome;
        File arquivoIL = new File(arquivoFonte.getParentFile(), nomeBase + ".il");

        PrintWriter writer = new PrintWriter(arquivoIL);
        writer.print(codigoObjeto);
        writer.close();
    }

    private void configurarAcoes(JButton btnNovo, JButton btnAbrir, JButton btnSalvar, JButton btnCopiar, JButton btnColar, JButton btnRecortar, JButton btnCompilar, JButton btnEquipe) {

        Action acaoNovo = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                editor.setText("");
                mensagens.setText("");
                status.setText(" ");
                caminhoArquivo = null;
            }
        };

        Action acaoAbrir = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto (*.txt)", "txt"));
                if (chooser.showOpenDialog(Mainframe.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File arquivo = chooser.getSelectedFile();
                        StringBuilder texto = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new FileReader(arquivo));
                        String linha;
                        while ((linha = reader.readLine()) != null) {
                            texto.append(linha).append("\n");
                        }
                        reader.close();

                        editor.setText(texto.toString());
                        mensagens.setText("");
                        caminhoArquivo = arquivo.getAbsolutePath();
                        status.setText(caminhoArquivo);
                    } catch (Exception ex) {
                        mensagens.setText("Erro ao abrir arquivo.");
                    }
                }
            }
        };

        Action acaoSalvar = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (caminhoArquivo == null) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto (*.txt)", "txt"));
                        if (chooser.showSaveDialog(Mainframe.this) == JFileChooser.APPROVE_OPTION) {
                            caminhoArquivo = chooser.getSelectedFile().getAbsolutePath();
                            if (!caminhoArquivo.toLowerCase().endsWith(".txt")) {
                                caminhoArquivo += ".txt";
                            }
                        } else {
                            return;
                        }
                    }
                    PrintWriter writer = new PrintWriter(caminhoArquivo);
                    writer.print(editor.getText());
                    writer.close();

                    mensagens.setText("");
                    status.setText(caminhoArquivo);
                } catch (Exception ex) {
                    mensagens.setText("Erro ao salvar arquivo.");
                }
            }
        };

        Action acaoCompilar = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mensagens.setText("");

                String codigoFonte = editor.getText();

                try {
                    Lexico lexico = new Lexico(codigoFonte);
                    Sintatico sintatico = new Sintatico();
                    Semantico semantico = new Semantico();

                    sintatico.parse(lexico, semantico);
                    gerarArquivoIL(semantico.getCodigoObjeto());

                    mensagens.setText("programa compilado com sucesso");

                } catch (LexicalError ex) {
                    int linhaErro = calcularLinha(codigoFonte, ex.getPosition());
                    mensagens.setText("linha " + linhaErro + ": erro léxico");

                } catch (SyntaticError ex) {
                    int linhaErro = calcularLinha(codigoFonte, ex.getPosition());
                    mensagens.setText("linha " + linhaErro + ": erro sintático");

                } catch (SemanticError ex) {
                    int linhaErro = calcularLinha(codigoFonte, ex.getPosition());
                    mensagens.setText("linha " + linhaErro + ": " + ex.getMessage());

                } catch (IOException ex) {
                    mensagens.setText("Erro ao gerar arquivo .il.");
                }
            }
        };

        Action acaoEquipe = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                mensagens.setText("Equipe:\nAnderson Lucindo dos Santos\nNicolas Caresia\nThiago Rodrigo Schlei");
            }
        };

        btnNovo.addActionListener(acaoNovo);
        btnAbrir.addActionListener(acaoAbrir);
        btnSalvar.addActionListener(acaoSalvar);
        btnCompilar.addActionListener(acaoCompilar);
        btnEquipe.addActionListener(acaoEquipe);

        btnCopiar.addActionListener(e -> editor.copy());
        btnColar.addActionListener(e -> editor.paste());
        btnRecortar.addActionListener(e -> editor.cut());

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "Novo");
        actionMap.put("Novo", acaoNovo);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "Abrir");
        actionMap.put("Abrir", acaoAbrir);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "Salvar");
        actionMap.put("Salvar", acaoSalvar);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "Copiar");
        actionMap.put("Copiar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnCopiar.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "Colar");
        actionMap.put("Colar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnColar.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), "Recortar");
        actionMap.put("Recortar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnRecortar.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "Compilar");
        actionMap.put("Compilar", acaoCompilar);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Equipe");
        actionMap.put("Equipe", acaoEquipe);
    }
}

class IconeGerado implements Icon {

    private final int tamanho = 24;
    private final Color corFundo;
    private final String simbolo;

    public IconeGerado(Color corFundo, String simbolo) {
        this.corFundo = corFundo;
        this.simbolo = simbolo;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(corFundo);
        g2d.fillRoundRect(x, y, tamanho, tamanho, 6, 6);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int larguraTexto = fm.stringWidth(simbolo);
        int alturaTexto = fm.getAscent();

        g2d.drawString(simbolo, x + (tamanho - larguraTexto) / 2, y + (tamanho - fm.getHeight()) / 2 + alturaTexto);
        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return tamanho;
    }

    @Override
    public int getIconHeight() {
        return tamanho;
    }
}

class BarraSutil extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(190, 190, 190);
        this.trackColor = new Color(245, 245, 245);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        g2.fillRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2);
        g2.dispose();
    }
}