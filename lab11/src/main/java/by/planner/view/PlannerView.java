package by.planner.view;

import by.planner.model.PlannerService;
import by.planner.model.SortOrder;
import by.planner.model.StudyTask;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class PlannerView {
    private final JFrame frame = new JFrame("Планировщик учебных задач");
    private final TaskTable model = new TaskTable();
    private final JTable table = new JTable(model);
    private final JComboBox<SortOrder> order = new JComboBox<>(SortOrder.values());
    private final JButton add = new JButton("Добавить");
    private final JButton edit = new JButton("Изменить");
    private final JButton toggle = new JButton("Завершить");
    private final JButton remove = new JButton("Удалить");
    private final JButton summary = new JButton("Сводка");

    public PlannerView() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(225);
        table.getSelectionModel().addListSelectionListener(event -> updateSelection());
        updateSelection();

        var toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 4));
        toolbar.add(add);
        toolbar.add(edit);
        toolbar.add(toggle);
        toolbar.add(remove);
        toolbar.add(summary);
        var sortLabel = new JLabel("Сортировка");
        sortLabel.setLabelFor(order);
        toolbar.add(sortLabel);
        toolbar.add(order);

        var content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(toolbar, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(740, 340));
        frame.setSize(850, 465);
        frame.setLocationRelativeTo(null);
    }

    public void bind(Runnable addAction, Runnable editAction, Runnable toggleAction,
                     Runnable removeAction, Runnable summaryAction, Runnable sortAction) {
        add.addActionListener(event -> addAction.run());
        edit.addActionListener(event -> editAction.run());
        toggle.addActionListener(event -> toggleAction.run());
        remove.addActionListener(event -> removeAction.run());
        summary.addActionListener(event -> summaryAction.run());
        order.addActionListener(event -> sortAction.run());
    }

    public void open(Runnable afterClose) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                afterClose.run();
            }
        });
        frame.setVisible(true);
    }

    public SortOrder order() { return (SortOrder) order.getSelectedItem(); }

    public StudyTask selected() {
        int row = table.getSelectedRow();
        return row < 0 || row >= model.tasks.size() ? null : model.tasks.get(row);
    }

    public void showTasks(List<StudyTask> tasks, UUID selectedId) {
        model.tasks = List.copyOf(tasks);
        model.fireTableDataChanged();
        for (int row = 0; row < tasks.size(); row++) {
            if (tasks.get(row).id().equals(selectedId)) {
                table.setRowSelectionInterval(row, row);
                break;
            }
        }
        updateSelection();
    }

    public void showEditor(StudyTask task, Function<TaskEditor.Input, Boolean> save) {
        new TaskEditor(frame, task, save).setVisible(true);
    }

    public void showSummary(PlannerService.Summary values) {
        String message = """
                Всего задач: %d
                Завершено: %d
                Осталось времени: %d мин.
                """.formatted(values.total(), values.completed(), values.remainingMinutes());
        JOptionPane.showMessageDialog(frame, message, "Сводка", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmRemoval() {
        return JOptionPane.showConfirmDialog(frame, "Удалить выбранную задачу?", "Удаление задачи",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void showError(String message) {
        Window focused = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        JOptionPane.showMessageDialog(focused == null ? frame : focused, message, "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }

    private void updateSelection() {
        var task = selected();
        edit.setEnabled(task != null);
        toggle.setEnabled(task != null);
        remove.setEnabled(task != null);
        toggle.setText(task != null && task.completed() ? "Возобновить" : "Завершить");
    }

    private static final class TaskTable extends AbstractTableModel {
        private static final String[] COLUMNS = {"Название", "Вид", "Срок", "Приоритет", "Минуты", "Статус"};
        private List<StudyTask> tasks = List.of();

        @Override
        public int getRowCount() { return tasks.size(); }

        @Override
        public int getColumnCount() { return COLUMNS.length; }

        @Override
        public String getColumnName(int column) { return COLUMNS[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            var task = tasks.get(row);
            return switch (column) {
                case 0 -> task.details().title();
                case 1 -> task.type();
                case 2 -> task.details().dueDate();
                case 3 -> task.details().priority();
                case 4 -> task.estimatedMinutes();
                case 5 -> task.completed() ? "Завершена" : "Открыта";
                default -> throw new IndexOutOfBoundsException(column);
            };
        }
    }
}
