package view;

import controller.TaskController;
import model.User;
import model.abstractes.TaskBase;
import model.AcademicTask;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Locale;

public class DashboardPanel extends BasePanel {
    private JPanel deadlinesPanel;

    // Layout constants
    private final int CARD_WIDTH = 175;
    private final int CARD_HEIGHT = 80;
    private final int CARDS_PER_ROW = 4;
    private final int CARD_SPACING = 10;

    // Additional colors specific to dashboard
    private final Color TASK_CARD_COLOR = new Color(220, 180, 220);
    private final Color DATE_HEADER_COLOR = new Color(150, 50, 200);

    public DashboardPanel(MainFrame mainFrame, User user) {
        super(mainFrame, user);
        initDashboardComponents();
        setupDashboardLayout();
        loadUpcomingTasks();
        checkDeadlineReminders();
    }

    private void initDashboardComponents() {
        // Initialize deadlines panel dengan padding yang tepat
        deadlinesPanel = new JPanel();
        deadlinesPanel.setLayout(new BoxLayout(deadlinesPanel, BoxLayout.Y_AXIS));
        deadlinesPanel.setBackground(BACKGROUND_COLOR);
        // Kurangi padding kanan untuk mencegah terpotong
        deadlinesPanel.setBorder(new EmptyBorder(20, 20, 20, 0));
    }

    // Custom ScrollBar UI untuk menyembunyikan scrollbar
    private class InvisibleScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(0, 0, 0, 0); // Transparent
            this.trackColor = new Color(0, 0, 0, 0); // Transparent
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Don't paint track
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            // Don't paint thumb
        }
    }

    private JScrollPane createInvisibleScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);

        // Set custom UI untuk vertical scrollbar
        scrollPane.getVerticalScrollBar().setUI(new InvisibleScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new InvisibleScrollBarUI());

        // Additional settings
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Set scrollbar width to 0 untuk benar-benar menyembunyikan
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        // Enable mouse wheel scrolling
        scrollPane.setWheelScrollingEnabled(true);

        return scrollPane;
    }

    private void setupDashboardLayout() {
        // Header panel dengan welcome message digabung
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(HEADER_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setOpaque(false);

        // Dashboard title di kiri
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Welcome message di kanan
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.LIGHT_GRAY);
        welcomeLabel.setBorder(new EmptyBorder(0, 20, 0, 20));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Deadlines title
        JLabel deadlinesTitle = new JLabel("Deadlines");
        deadlinesTitle.setFont(new Font("Arial", Font.BOLD, 24));
        deadlinesTitle.setForeground(TEXT_COLOR);
        deadlinesTitle.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Scroll pane for deadlines dengan invisible scrollbar
        JScrollPane scrollPane = createInvisibleScrollPane(deadlinesPanel);

        mainPanel.add(deadlinesTitle, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom navigation panel
        JPanel navPanel = createNavigationPanel();

        // Layout utama - langsung gunakan headerPanel tanpa welcome panel terpisah
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
    }

    private void loadUpcomingTasks() {
        deadlinesPanel.removeAll();

        List<TaskBase> upcomingTasks = TaskController.getUpcomingDeadlines(currentUser.getUserId(), 14);

        if (upcomingTasks.isEmpty()) {
            JLabel noTasksLabel = new JLabel("Tidak ada tugas mendatang");
            noTasksLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noTasksLabel.setForeground(TEXT_COLOR.brighter());
            noTasksLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noTasksLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            deadlinesPanel.add(noTasksLabel);
        } else {
            // Group tasks by date
            Map<LocalDate, List<TaskBase>> tasksByDate = upcomingTasks.stream()
                .collect(Collectors.groupingBy(TaskBase::getDeadline));

            // Sort dates
            tasksByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    LocalDate date = entry.getKey();
                    List<TaskBase> tasks = entry.getValue();

                    // Add date header
                    deadlinesPanel.add(createDateHeader(date));
                    deadlinesPanel.add(Box.createVerticalStrut(10));

                    // Add task cards dengan grid layout yang responsif
                    deadlinesPanel.add(createTaskGrid(tasks));
                    deadlinesPanel.add(Box.createVerticalStrut(20));
                });
        }

        deadlinesPanel.revalidate();
        deadlinesPanel.repaint();
    }

    private JPanel createTaskGrid(List<TaskBase> tasks) {
        JPanel gridPanel = new JPanel();
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));

        // Calculate rows needed
        int totalTasks = tasks.size();
        int rows = (int) Math.ceil((double) totalTasks / CARDS_PER_ROW);

        for (int row = 0; row < rows; row++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, CARD_SPACING, 0));
            rowPanel.setBackground(BACKGROUND_COLOR);

            // Calculate tasks for this row
            int startIndex = row * CARDS_PER_ROW;
            int endIndex = Math.min(startIndex + CARDS_PER_ROW, totalTasks);
            int tasksInThisRow = endIndex - startIndex;

            // Add tasks to this row
            for (int i = startIndex; i < endIndex; i++) {
                TaskBase task = tasks.get(i);
                JPanel taskCard = createTaskCard(task, tasksInThisRow);
                rowPanel.add(taskCard);
            }

            gridPanel.add(rowPanel);

            // Add spacing between rows if not the last row
            if (row < rows - 1) {
                gridPanel.add(Box.createVerticalStrut(CARD_SPACING));
            }
        }

        return gridPanel;
    }

    private JPanel createDateHeader(LocalDate date) {
        // Container panel untuk mengatur margin
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);
        containerPanel.setOpaque(false);

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(DATE_HEADER_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.dispose();
            }
        };

        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(0, 35));
        headerPanel.setLayout(new BorderLayout());

        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        String dateStr = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", new Locale("id", "ID")));

        JLabel dateLabel = new JLabel(dayName + ", " + dateStr);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setBorder(new EmptyBorder(0, 15, 0, 15));

        headerPanel.add(dateLabel, BorderLayout.WEST);

        // Tambahkan margin kanan untuk mencegah border radius terpotong
        containerPanel.add(headerPanel, BorderLayout.CENTER);
        containerPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Set maximum size untuk mencegah stretching
        containerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        return containerPanel;
    }

    private JPanel createTaskCard(TaskBase task, int tasksInRow) {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Add hover effect
                if (getMousePosition() != null) {
                    g2.setColor(TASK_CARD_COLOR.brighter());
                } else {
                    g2.setColor(TASK_CARD_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.dispose();
            }
        };

        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Set fixed size untuk konsistensi
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setMinimumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        // Course name or category
        String courseName = "";
        if (task instanceof AcademicTask) {
            courseName = TaskController.getCourseName(((AcademicTask) task).getCourseId());
        } else {
            courseName = "Personal";
        }

        JLabel courseLabel = new JLabel(courseName);
        courseLabel.setFont(new Font("Arial", Font.BOLD, 12));
        courseLabel.setForeground(TEXT_COLOR);

        JLabel taskLabel = new JLabel("<html>" + task.getTitle() + "</html>");
        taskLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        taskLabel.setForeground(TEXT_COLOR.brighter());
        taskLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(courseLabel, BorderLayout.NORTH);
        textPanel.add(taskLabel, BorderLayout.CENTER);

        cardPanel.add(textPanel, BorderLayout.CENTER);

        // Add click handler
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open task detail dialog
                new TaskDetailDialog(mainFrame, task, DashboardPanel.this).setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cardPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cardPanel.repaint();
            }
        });

        return cardPanel;
    }

    private void checkDeadlineReminders() {
        List<TaskBase> urgentTasks = TaskController.getUpcomingDeadlines(currentUser.getUserId(), 3);

        if (!urgentTasks.isEmpty()) {
            StringBuilder message = new StringBuilder("Tugas dengan deadline mendekat:\n\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (TaskBase task : urgentTasks) {
                message.append("• ").append(task.getTitle())
                       .append(" (").append(task.getDeadline().format(formatter)).append(")\n");
            }

            JOptionPane.showMessageDialog(this, message.toString(), "Pengingat Deadline", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Override BasePanel abstract methods
    @Override
    protected void handleAdd(ActionEvent e) {
        mainFrame.showTaskManagementPanel();
    }

    @Override
    public void refreshData() {
        loadUpcomingTasks();
    }

    // Override navigation to highlight current page
    @Override
    protected void handleHome(ActionEvent e) {
        // Already on home, just refresh
        refreshData();
    }
}
