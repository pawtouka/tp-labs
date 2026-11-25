package by.planner.view;

import by.planner.model.Priority;
import by.planner.model.StudyTask;
import by.planner.model.TaskType;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.function.Function;

public final class TaskEditor extends JDialog {
    public record Input(TaskType type, String title, String dueDate, Priority priority, String units) { }

    TaskEditor(JFrame parent, StudyTask task, Function<Input, Boolean> save) {
        super(parent, task == null ? "Новая задача" : "Изменить задачу", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        var title = new JTextField(24);
        var type = new JComboBox<>(TaskType.values());
        var date = new JTextField(LocalDate.now().toString(), 14);
        var priority = new JComboBox<>(Priority.values());
        priority.setSelectedItem(Priority.NORMAL);
        var units = new JTextField("1", 6);
        var unitsLabel = new JLabel();
        unitsLabel.setLabelFor(units);

        if (task != null) {
            var details = task.details();
            title.setText(details.title());
            type.setSelectedItem(task.type());
            date.setText(details.dueDate().toString());
            priority.setSelectedItem(details.priority());
            units.setText(Integer.toString(details.units()));
        }

        Runnable updateUnits = () -> unitsLabel.setText(((TaskType) type.getSelectedItem()).unit());
        type.addActionListener(event -> updateUnits.run());
        updateUnits.run();

        var form = new JPanel(new GridLayout(5, 2, 12, 8));
        addField(form, "Название", title);
        addField(form, "Вид задачи", type);
        addField(form, "Срок (ГГГГ-ММ-ДД)", date);
        addField(form, "Приоритет", priority);
        form.add(unitsLabel);
        form.add(units);

        var submit = new JButton("Сохранить");
        submit.addActionListener(event -> {
            var input = new Input((TaskType) type.getSelectedItem(), title.getText(), date.getText(),
                    (Priority) priority.getSelectedItem(), units.getText());
            if (save.apply(input)) {
                dispose();
            }
        });
        var cancel = new JButton("Отмена");
        cancel.addActionListener(event -> dispose());
        var buttons = new JPanel();
        buttons.add(submit);
        buttons.add(cancel);

        var content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));
        content.add(form, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);
        getRootPane().setDefaultButton(submit);
        pack();
        setLocationRelativeTo(parent);
    }

    private static void addField(JPanel form, String label, JComponent field) {
        var caption = new JLabel(label);
        caption.setLabelFor(field);
        form.add(caption);
        form.add(field);
    }
}
