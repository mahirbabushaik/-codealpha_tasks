import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class AIChatbotApp extends JFrame {

    // GUI Components
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    
    // NLP Intent Engine Data Structures
    private Map<String, String[]> intentKeywords;
    private Map<String, String> intentResponses;

    public AIChatbotApp() {
        // Initialize the NLP Brain
        initializeIntentEngine();

        // Configure GUI Window
        setTitle("AI Chatbot Interface");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Create Chat Log Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(new Color(245, 245, 250));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create Input Panel (TextField + Button)
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to Frame
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Action Listeners for Real-time Interaction
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processUserMessage();
            }
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processUserMessage();
                }
            }
        });

        // Initial Welcome Message
        appendMessage("Bot", "Hello! I am your Intent-Based AI Assistant. Ask me anything about Java or NLP!");
    }

    /**
     * Trains the Bot's Brain by grouping keywords into distinct Intents
     */
    private void initializeIntentEngine() {
        intentKeywords = new HashMap<>();
        intentResponses = new HashMap<>();

        // Intent 1: Greetings
        intentKeywords.put("GREETING", new String[]{"hi", "hello", "hey", "greetings", "whats up"});
        intentResponses.put("GREETING", "Hello! How can I assist you with your queries today?");

        // Intent 2: Farewells
        intentKeywords.put("FAREWELL", new String[]{"bye", "goodbye", "exit", "see ya"});
        intentResponses.put("FAREWELL", "Goodbye! Have a stellar day ahead.");

        // Intent 3: Java Programming
        intentKeywords.put("JAVA_TOPIC", new String[]{"java", "oop", "language", "code", "object", "class"});
        intentResponses.put("JAVA_TOPIC", "Java is a robust, class-based, object-oriented programming language widely used for building enterprise applications.");

        // Intent 4: NLP Concepts
        intentKeywords.put("NLP_TOPIC", new String[]{"nlp", "natural", "language", "processing", "text", "tokens"});
        intentResponses.put("NLP_TOPIC", "Natural Language Processing (NLP) bridges human language and machine understanding through pipelines like tokenization and normalization.");

        // Intent 5: General Help
        intentKeywords.put("HELP", new String[]{"help", "support", "options", "what", "do"});
        intentResponses.put("HELP", "I can answer questions regarding basic Java programming, core NLP workflows, or my own project setup. Fire away!");
        
        // Intent 6: Appreciation
        intentKeywords.put("THANKS", new String[]{"thanks", "thank", "awesome", "cool"});
        intentResponses.put("THANKS", "You're very welcome! Let me know if you need anything else.");
    }

    /**
     * Processing Pipeline: Clean Text -> Track Overlap Scores -> Pick Winning Intent
     */
    private String processNLPPipeline(String rawInput) {
        // 1. Normalization
        String cleanedInput = rawInput.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").trim();

        String bestIntent = null;
        int maxScore = 0;

        // 2. Intent Scoring Loop
        for (Map.Entry<String, String[]> entry : intentKeywords.entrySet()) {
            int currentScore = 0;
            for (String keyword : entry.getValue()) {
                // Check if the cleaned message contains the keyword
                if (cleanedInput.contains(keyword)) {
                    currentScore++;
                }
            }

            // High score wins the context mapping
            if (currentScore > maxScore) {
                maxScore = currentScore;
                bestIntent = entry.getKey();
            }
        }

        // 3. Fallback execution if no intents matched
        return (maxScore > 0) ? intentResponses.get(bestIntent) : "I'm sorry, I couldn't quite track that intent. Could you try phrasing it differently?";
    }

    /**
     * Appends UI text strings safely and resets state
     */
    private void processUserMessage() {
        String userText = inputField.getText().trim();
        if (!userText.isEmpty()) {
            appendMessage("You", userText);
            inputField.setText("");

            // Run message through the Intent engine
            String botResponse = processNLPPipeline(userText);
            
            // Swing Timer simulates real-time typing/thinking latency
            Timer timer = new Timer(350, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    appendMessage("Bot", botResponse);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Appends formatted lines to the scrolling text frame
     */
    private void appendMessage(String sender, String text) {
        chatArea.append(sender + ": " + text + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        // Safe UI Thread Launch
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AIChatbotApp().setVisible(true);
            }
        });
    }
}