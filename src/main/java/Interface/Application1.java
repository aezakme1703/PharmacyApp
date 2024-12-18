package Interface;

import MyExceptions.NegativeIntException;
import MyExceptions.NotFoundInDatabase;
import MyExceptions.OnlyLettersException;
import MyExceptions.OtherFormats;
import PharmacyClasses.Medicine;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
/** Класс таблицы с лекарствами */
public class Application1 {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test_persistence");
    EntityManager em = emf.createEntityManager();
    private static final Logger logger = LogManager.getLogger(Application1.class);
    //Таблицы с данными
    private static boolean isEditable = false;
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return isEditable;
        }
    };
    JFrame pharmacy = new JFrame("Аптека");
    JTable medicine = new JTable(model);
    /** Метод создания окна с таблицей о лекарствах*/
    public void Interface() throws IOException {
        logger.info("Открытие таблицы с лекарствами");
        //Создание окна
        pharmacy.setLayout(new BorderLayout());
        pharmacy.setSize(1080, 700);
        pharmacy.setLocation(100, 100);
        pharmacy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pharmacy.setVisible(true);
        //Создание панели инструментов
        JToolBar toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        SpringLayout layout = new SpringLayout();
        toolBar.setLayout(layout);
        pharmacy.add(toolBar);
        //Создание кнопок и прикрепление иконок
        JButton save = new JButton(new ImageIcon("Icons/save1.png"));
        JButton open = new JButton(new ImageIcon("Icons/open1.png"));
        JButton addInf = new JButton(new ImageIcon("Icons/plus1.png"));
        JButton deleteInf = new JButton(new ImageIcon("Icons/minus1.png"));
        JButton edit = new JButton(new ImageIcon("Icons/edit1.png"));
        JButton print = new JButton(new ImageIcon("Icons/print1.png"));
        JButton search = new JButton(new ImageIcon("Icons/search1.png"));
        JButton chooseTable = new JButton(new ImageIcon("Icons/table1.png"));
        JButton updateTable = new JButton(new ImageIcon("Icons/update1.png"));


        // Подсказки для кнопок
        save.setToolTipText("Сохранить");
        open.setToolTipText("Открыть");
        addInf.setToolTipText("Добавить информацию");
        deleteInf.setToolTipText("Удалить информацию");
        edit.setToolTipText("Редактировать");
        print.setToolTipText("Печать");
        search.setToolTipText("Поиск");
        chooseTable.setToolTipText("Выбор таблицы");
        updateTable.setToolTipText("Обновить таблицу");

        // Добавление кнопок на панель инструментов
        toolBar.add(save);
        layout.putConstraint(SpringLayout.NORTH , save, 0,
                SpringLayout.NORTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , save, 0,
                SpringLayout.WEST , toolBar);
        toolBar.add(open);
        layout.putConstraint(SpringLayout.WEST , open, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , open, 1,
                SpringLayout.SOUTH , save);
        toolBar.add(addInf);
        layout.putConstraint(SpringLayout.WEST , addInf, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , addInf, 1,
                SpringLayout.SOUTH , open);
        toolBar.add(deleteInf);
        layout.putConstraint(SpringLayout.WEST , deleteInf, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , deleteInf, 1,
                SpringLayout.SOUTH , addInf);
        toolBar.add(search);
        layout.putConstraint(SpringLayout.WEST , search, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , search, 1,
                SpringLayout.SOUTH , deleteInf);
        toolBar.add(edit);
        layout.putConstraint(SpringLayout.WEST , edit, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , edit, 1,
                SpringLayout.SOUTH , search);
        toolBar.add(print);
        layout.putConstraint(SpringLayout.WEST , print, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , print, 1,
                SpringLayout.SOUTH , edit);
        toolBar.add(chooseTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseTable, 0,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseTable, 0,
                SpringLayout.WEST , toolBar);

        toolBar.add(updateTable);
        layout.putConstraint(SpringLayout.SOUTH , updateTable, 0,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.EAST , updateTable, 0,
                SpringLayout.EAST , toolBar);


        medicine.getTableHeader().setReorderingAllowed(false);
        model.addColumn("ID");
        model.addColumn("Название");
        model.addColumn("Цена");
        model.addColumn("Количество");
        model.addColumn("Наличие");
        // Получаем список всех элементов таблицы
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Medicine> cq = cb.createQuery(Medicine.class);
        Root<Medicine> rootEntry = cq.from(Medicine.class);
        List<Order> orderList = new ArrayList<>();
        orderList.add(cb.desc(rootEntry.get("medId")));
        CriteriaQuery<Medicine> all = cq.orderBy(orderList);
        TypedQuery<Medicine> allQuery = em.createQuery(all);
        List<Medicine> meds = allQuery.getResultList();
        // Выводим на экран элементы таблицы
        for (Medicine medDownload : meds) {
            String avail = medDownload.getAvailability() ? "Да":"Нет";
            model.insertRow(0, new Object[]{medDownload.getMedId(), medDownload.getNameOfMedicine(),
                    medDownload.getPrice(), medDownload.getCountOfMedicine(), avail});
        }
        JScrollPane scroll = new JScrollPane(medicine);
        toolBar.add(scroll);
        layout.putConstraint(SpringLayout.NORTH , scroll, 0,
                SpringLayout.SOUTH , save);
        layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.EAST, save);
        layout.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST, toolBar);
        layout.putConstraint(SpringLayout.SOUTH, scroll, -40, SpringLayout.SOUTH, toolBar);
        layout.putConstraint(SpringLayout.NORTH, scroll, 0, SpringLayout.NORTH, toolBar);
        toolBar.revalidate();
        toolBar.repaint();

        //Действия кнопок
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonSave();
            }
        });
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonOpen();
            }
        });
        print.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonPrint();
            }
        });
        addInf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonAddInf();
            }
        });
        deleteInf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonDeleteInf();
            }
        });
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonSearchInf();
            }
        });
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonEditInf();
            }
        });
        updateTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });


        JButton chooseDisTable = new JButton("Болезни");
        toolBar.add(chooseDisTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseDisTable, -5,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseDisTable, 50,
                SpringLayout.WEST , chooseTable);
        chooseDisTable.setVisible(false);

        JButton chooseSaleTable = new JButton("Операции");
        toolBar.add(chooseSaleTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseSaleTable, -5,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseSaleTable, 63,
                SpringLayout.WEST , chooseDisTable);
        chooseSaleTable.setVisible(false);

        final boolean[] isTableVisible = {false};
        chooseTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isTableVisible[0]) {
                    chooseDisTable.setVisible(true);
                    chooseSaleTable.setVisible(true);

                } else {
                    chooseDisTable.setVisible(false);
                    chooseSaleTable.setVisible(false);
                }
                isTableVisible[0] = !isTableVisible[0];
            }
        });
        chooseDisTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pharmacy.setVisible(false);
                new DiseaseTable().show();
            }
        });
        chooseSaleTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pharmacy.setVisible(false);
                new SalesTable().show();
            }
        });

    }
    /** Метод для сохранения таблицы с лекарствами в XML файл*/
    public void ButtonSave() {
        logger.info("Сохранение таблицы лекарств");
        String fileName = "E:\\Student\\2st course\\OOP\\ReportExample\\Meds.xml";
        try {
            if (fileName.toLowerCase().endsWith(".xml")){
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.newDocument();
                Node pharmacy1 = doc.createElement("medlist");
                doc.appendChild(pharmacy1);
                for (int i = 0; i < model.getRowCount(); i++) {
                    Element medicine = doc.createElement("medicine");
                    pharmacy1.appendChild(medicine);
                    medicine.setAttribute("id",  String.valueOf(model.getValueAt(i, 0)));
                    medicine.setAttribute("name", String.valueOf(model.getValueAt(i, 1)));
                    medicine.setAttribute("price", String.valueOf(model.getValueAt(i, 2)));
                    medicine.setAttribute("count", String.valueOf(model.getValueAt(i, 3)));
                    medicine.setAttribute("availability", String.valueOf(model.getValueAt(i, 4)));
                }
                Transformer trans = TransformerFactory.newInstance().newTransformer();
                trans.transform(new DOMSource(doc), new StreamResult(writer));
                JOptionPane.showMessageDialog (pharmacy, "Файл успешно сохранен");
            }
            else{
                logger.error("Ошибка при сохранении таблицы лекарств (неверный формат)");
                throw new OtherFormats("Введен неверный формат. Сохранение данных возможно только в формате XML.");
            }

        }
        catch (ParserConfigurationException | IOException | TransformerException e) { e.printStackTrace(); }
        catch (OtherFormats e) {JOptionPane.showMessageDialog(pharmacy, e.getMessage());}


    }
    /** Метод для открытия и получения данных о лекарствах из XML файла*/
    public void ButtonOpen() {
        logger.info("Открытие таблицы лекарств");
        FileDialog open = new FileDialog(pharmacy, "Получение данных", FileDialog.LOAD);
        open.setFile("*.xml");
        open.setVisible(true);
        String fileName = open.getDirectory() + open.getFile();
        if (open.getFile() == null) {
            return;
        }
        try {
            if (fileName.toLowerCase().endsWith(".xml")){
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = dBuilder.parse(new File(fileName));
                doc.getDocumentElement().normalize();
                NodeList nlMedicines = doc.getElementsByTagName("medicine");

                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Medicine> cq = cb.createQuery(Medicine.class);
                Root<Medicine> rootEntry = cq.from(Medicine.class);
                List<Order> orderList = new ArrayList<>();
                orderList.add(cb.desc(rootEntry.get("medId")));
                CriteriaQuery<Medicine> all = cq.orderBy(orderList);
                TypedQuery<Medicine> allQuery = em.createQuery(all);
                List<Medicine> meds = allQuery.getResultList();
                boolean anyAdding = false;
                for (int temp = 0; temp < nlMedicines.getLength(); temp++) {
                    Node elem = nlMedicines.item(temp);
                    NamedNodeMap attrs = elem.getAttributes();
                    String name = attrs.getNamedItem("name").getNodeValue();
                    String price = attrs.getNamedItem("price").getNodeValue();
                    String count = attrs.getNamedItem("count").getNodeValue();
                    em.getTransaction().begin();
                    boolean needToAddMed = true;

                    for (Medicine medDownload : meds) {
                        if (medDownload.getNameOfMedicine().equals(name)){
                            needToAddMed = false;
                            break;
                        }
                    }
                    if (needToAddMed) {
                        anyAdding = true;
                        Medicine med = new Medicine();
                        med.setNameOfMedicine(name);
                        med.setPrice(Integer.parseInt(price));
                        med.setCountOfMedicine(Integer.parseInt(count));
                        med.setAvailability(Integer.parseInt(count) > 0);
                        em.persist(med);
                        updateTable();
                    }
                    em.getTransaction().commit();
                }

                if (anyAdding){
                    JOptionPane.showMessageDialog (pharmacy, "Новые записи успешно добавлены");
                }
                else{JOptionPane.showMessageDialog (pharmacy, "Новых записей не найдено");}
            } else{
                logger.error("Ошибка при открытии таблицы лекарств(неверный формат)");
                throw new OtherFormats("Введен неверный формат. Сохранение данных возможно только в формате XML.");
            }


        } catch (ParserConfigurationException | SAXException | IOException | OtherFormats e) {
            e.printStackTrace();
        }

    }
    /** Метод для формирования отчета о лекарствах*/
    public void ButtonPrint() {
        logger.info("Формирование отчета о лекарствах");
        FileDialog saveReport = new FileDialog(pharmacy, "Формирование отчета", FileDialog.SAVE);
        saveReport.setVisible(true);
        String fileName = saveReport.getDirectory() + saveReport.getFile();
        if (saveReport.getFile() == null) {
            return;
        }
        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                printMethod("E:\\Student\\2st course\\OOP\\ReportExample\\Meds.xml", "E:\\Student\\2st course\\OOP\\ReportExample\\reportMed.jrxml", fileName);
            } else {
                logger.error("Ошибка при формировании отчета о лекарствах (неверный формат)");
                throw new OtherFormats("Введен неверный формат. Формирование отчета возможно только в формате PDF.");
            }
            JOptionPane.showMessageDialog (pharmacy, "Отчёт успешно сформирован");
        } catch (OtherFormats e) {
            JOptionPane.showMessageDialog(pharmacy, e.getMessage());
        }
    }
    /** Метод для добавления информации о лекарстве*/
    public void ButtonAddInf(){
        logger.info("Добавление информации о лекарствах");
        JFrame InformationAdd;
        JButton ButtonAdd;
        JToolBar panel;

        InformationAdd = new JFrame("Добавление информации");
        InformationAdd.setSize(250, 300);
        InformationAdd.setLocation(1175, 100);
        panel = new JToolBar();
        panel.setLayout(new GridBagLayout());
        panel.setFloatable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        InformationAdd.add(panel);
        InformationAdd.setVisible(true);
        // Настройка отступов
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; // колонка
        gbc.gridy = 4; // строка
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Название"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        JTextArea fieldName = new JTextArea(1,10);
        fieldName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneName = new JScrollPane(fieldName);
        scrollPaneName.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPaneName, gbc);

        gbc.gridx = 0; // колонка
        gbc.gridy = 7; // строка
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Цена"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        JTextArea fieldPrice = new JTextArea(1,10);
        fieldPrice.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPanePrice = new JScrollPane(fieldPrice);
        scrollPanePrice.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPanePrice, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Количество"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        JTextArea fieldCount = new JTextArea(1,10);
        fieldCount.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneCount = new JScrollPane(fieldCount);
        scrollPaneCount.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPaneCount, gbc);

        ButtonAdd = new JButton("Добавить");
        gbc.gridx = 1;
        gbc.gridy = 40;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(ButtonAdd, gbc);


        ButtonAdd.addActionListener (new ActionListener()
        {
            public void actionPerformed (ActionEvent event)
            {
                try {
                    logger.debug("Проверка введенных значений информации о лекарстве");
                    checkField(fieldName, false, "Название");
                    String textName = fieldName.getText();
                    checkField(fieldPrice, true, "Цена");
                    String textPrice = fieldPrice.getText();
                    if (Integer.parseInt(textPrice) == 0){
                        throw new NegativeIntException("Цена лекарства не может быть нулевой");
                    }
                    checkField(fieldCount, true, "Количество");
                    String textCount = fieldCount.getText();
                    em.getTransaction().begin();
                    Medicine med = new Medicine();
                    med.setNameOfMedicine(textName);
                    med.setPrice(Integer.parseInt(textPrice));
                    med.setCountOfMedicine(Integer.parseInt(textCount));
                    med.setAvailability(Integer.parseInt(textCount) > 0);
                    em.persist(med);
                    em.getTransaction().commit();
                    fieldName.setText(null);
                    fieldPrice.setText(null);
                    fieldCount.setText(null);
                    InformationAdd.setVisible(false);
                    updateTable();
                    JOptionPane.showMessageDialog(pharmacy, "Лекарство успешно добавлено");
                } catch (NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e) {
                    JOptionPane.showMessageDialog(pharmacy, e.getMessage());
                }
            }
        });


    }
    /** Метод для удаления информации о лекарстве*/
    public void ButtonDeleteInf(){
        logger.info("Удаление информации о лекарстве");
        JFrame InformationDelete;
        JButton ButtonDelete;
        JToolBar panelDelete;
        InformationDelete = new JFrame("Удаление информации");
        InformationDelete.setSize(300, 300);
        InformationDelete.setLocation(1150, 100);
        panelDelete = new JToolBar();
        panelDelete.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panelDelete.setFloatable(false);
        gbc.insets = new Insets(10, 10, 10, 10); // верхний, левый, нижний, правый отступ
        InformationDelete.add(panelDelete);
        InformationDelete.setVisible(true);
        gbc.gridx = 0; // колонка
        gbc.gridy = 4; // строка
        gbc.anchor = GridBagConstraints.CENTER;
        panelDelete.add(new JLabel("Введите ID лекарства"), gbc);
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea fieldDelete = new JTextArea(1,20);
        fieldDelete.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDelete = new JScrollPane(fieldDelete);
        scrollPaneDelete.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDelete.add(scrollPaneDelete, gbc);
        ButtonDelete = new JButton("Удалить");
        gbc.gridy = 50;
        gbc.anchor = GridBagConstraints.CENTER;
        panelDelete.add(ButtonDelete, gbc);
        // Добавляем MouseListener к таблице
        medicine.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = medicine.getSelectedRow();
                if (row != -1) {
                    Object idValue = medicine.getValueAt(row, 0);
                    fieldDelete.setText(idValue.toString());
                }
            }
        });

        ButtonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    logger.debug("Проверка введеного ID");
                    checkField(fieldDelete, true, "ID");
                    String STRFieldDelete = fieldDelete.getText();
                    int idDelete = Integer.parseInt(STRFieldDelete);
                    em.getTransaction().begin();
                    Medicine med = em.find(Medicine.class, idDelete);
                    if (med != null){
                        int option = JOptionPane.showConfirmDialog(
                                null,
                                "Вы уверены что хотите удалить выбранное лекарство?",
                                "Подтверждение удаления",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );
                        if (option == JOptionPane.YES_OPTION){
                            logger.debug("Удаление лекарства из таблицы");
                            em.remove(med);
                            em.getTransaction().commit();
                            updateTable();
                            InformationDelete.setVisible(false);
                            JOptionPane.showMessageDialog(pharmacy, "Лекарство удалено успешно");
                        } else{
                            em.getTransaction().commit();
                            InformationDelete.setVisible(false);}

                    }
                    else{
                        em.getTransaction().commit();
                        fieldDelete.setText(null);
                        throw new NotFoundInDatabase("Лекарство с данным ID не найдено");
                    }

                } catch (NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                    JOptionPane.showMessageDialog(pharmacy, e1.getMessage());
                }
            }
        });


    }
    /** Метод для поиска информации о лекарстве*/
    public void ButtonSearchInf(){
        logger.info("Поиск информации о лекарстве");
        JFrame frameSearch = new JFrame("Поиск информации");
        frameSearch.setSize(350, 300);
        frameSearch.setLocation(1170, 100);
        frameSearch.setVisible(true);

        JToolBar panelSearch;
        panelSearch = new JToolBar();
        SpringLayout layout = new SpringLayout();
        panelSearch.setLayout(layout);
        panelSearch.setFloatable(false);
        frameSearch.add(panelSearch);

        JLabel labelSearch = new JLabel("Введите название лекарства");
        panelSearch.add(labelSearch);
        layout.putConstraint(SpringLayout.NORTH , labelSearch, 80,
                SpringLayout.NORTH , panelSearch);
        layout.putConstraint(SpringLayout.WEST , labelSearch, 80,
                SpringLayout.WEST , panelSearch);

        JTextArea fieldSearchName = new JTextArea(1,15);
        fieldSearchName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneID = new JScrollPane(fieldSearchName);
        scrollPaneID.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelSearch.add(scrollPaneID);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneID, 130,
                SpringLayout.NORTH , panelSearch);
        layout.putConstraint(SpringLayout.WEST , scrollPaneID, 90,
                SpringLayout.WEST , panelSearch);

        JButton nextSearch = new JButton("Найти");
        panelSearch.add(nextSearch);
        layout.putConstraint(SpringLayout.NORTH , nextSearch, 170,
                SpringLayout.NORTH , panelSearch);
        layout.putConstraint(SpringLayout.WEST , nextSearch, 145,
                SpringLayout.WEST , panelSearch);

        medicine.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = medicine.getSelectedRow();
                if (row != -1) {
                    Object idValue = medicine.getValueAt(row, 1);
                    fieldSearchName.setText(idValue.toString());
                }
            }
        });

        nextSearch.addActionListener(e -> {
            try {
                logger.debug("Вывод найденных значений информации о лекарстве");
                checkField(fieldSearchName, false, "Название");
                String medName = fieldSearchName.getText();
                Medicine med = new Medicine();
                em.getTransaction().begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Medicine> cq = cb.createQuery(Medicine.class);
                Root<Medicine> rootEntry = cq.from(Medicine.class);
                List<Order> orderList = new ArrayList<>();
                orderList.add(cb.desc(rootEntry.get("medId")));
                CriteriaQuery<Medicine> all = cq.orderBy(orderList);
                TypedQuery<Medicine> allQuery = em.createQuery(all);
                List<Medicine> meds = allQuery.getResultList();
                boolean addNew = false;
                for (Medicine medDownload : meds) {
                    if (medName.equals(medDownload.getNameOfMedicine())){
                        med = em.find(Medicine.class, medDownload.getMedId());
                        em.getTransaction().commit();
                        addNew = true;
                        break;
                    }
                }
                if (!addNew){
                    em.getTransaction().commit();
                    throw new NotFoundInDatabase("Лекарство с введённым названием не найдено");
                }

                frameSearch.setSize(300, 250);
                frameSearch.setLocation(1050, 100);
                fieldSearchName.setVisible(false);
                scrollPaneID.setVisible(false);
                labelSearch.setVisible(false);
                nextSearch.setVisible(false);

                JLabel labelName = new JLabel("Название:");
                layout.putConstraint(SpringLayout.NORTH , labelName, 20,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelName, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelName);

                JLabel fieldName = new JLabel();
                fieldName.setText(med.getNameOfMedicine());
                fieldName.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldName, 20,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldName, 150,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldName);

                JLabel labelPrice = new JLabel("Цена:");
                layout.putConstraint(SpringLayout.NORTH , labelPrice, 50,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelPrice, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelPrice);

                JLabel fieldPrice = new JLabel();
                fieldPrice.setText(String.valueOf(med.getPrice()));
                fieldPrice.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldPrice, 50,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldPrice, 150,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldPrice);

                JLabel labelCount = new JLabel("Количество:");
                layout.putConstraint(SpringLayout.NORTH , labelCount, 80,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelCount, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelCount);

                JLabel fieldCount = new JLabel(String.valueOf(med.getCountOfMedicine()));
                fieldCount.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldCount, 80,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldCount, 150,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldCount);

                JLabel labelAvailability = new JLabel("Наличие:");
                layout.putConstraint(SpringLayout.NORTH , labelAvailability, 110,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelAvailability, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelAvailability);

                JLabel fieldAvailability = new JLabel();
                if (med.getAvailability()){
                    fieldAvailability.setText("Да");
                } else{fieldAvailability.setText("Нет");}
                fieldAvailability.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldAvailability, 110,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldAvailability, 150,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldAvailability);
                JLabel labelID = new JLabel("ID лекарства:");
                layout.putConstraint(SpringLayout.NORTH , labelID, 130,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelID, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelID);
                JLabel fieldID = new JLabel(String.valueOf(med.getMedId()));
                fieldID.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldID, 130,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldID, 150,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldID);

            } catch ( NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                JOptionPane.showMessageDialog(pharmacy, e1.getMessage());
            }
        });

    }
    /** Метод для редактирования информации о лекарстве*/
    public void ButtonEditInf(){
        logger.info("Редактирования информации о лекарстве");
        JFrame InformationEdit;
        JButton ButtonEdit;
        JToolBar panelEdit;
        JFrame FoundInformationEdit;
        InformationEdit = new JFrame("Редактирование информации");
        InformationEdit.setSize(350, 300);
        InformationEdit.setLocation(1175, 100);
        FoundInformationEdit = new JFrame("Редактирование информации");
        FoundInformationEdit.setSize(370, 250);
        FoundInformationEdit.setLocation(800, 100);
        panelEdit = new JToolBar();
        panelEdit.setLayout(new GridBagLayout());
        panelEdit.setFloatable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // верхний, левый, нижний, правый отступ
        gbc.gridx = 0; // колонка
        gbc.gridy = 4; // строка
        gbc.anchor = GridBagConstraints.CENTER;
        panelEdit.add(new JLabel("Введите ID лекарства для редактирования"), gbc);
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea fieldEdit = new JTextArea(1,20);
        fieldEdit.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        medicine.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = medicine.getSelectedRow();
                if (row != -1) {
                    Object idValue = medicine.getValueAt(row, 0);
                    fieldEdit.setText(idValue.toString());
                }
            }
        });
        JScrollPane scrollPaneEdit = new JScrollPane(fieldEdit);
        scrollPaneEdit.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelEdit.add(scrollPaneEdit, gbc);
        ButtonEdit = new JButton("Найти");
        gbc.gridy = 50;
        gbc.anchor = GridBagConstraints.CENTER;

        panelEdit.add(ButtonEdit, gbc);
        InformationEdit.add(panelEdit);
        InformationEdit.setVisible(true);

        ButtonEdit.addActionListener(e -> {
            try {
                checkField(fieldEdit, true, "ID");
                String STRFieldEdit = fieldEdit.getText();
                int idEdit = Integer.parseInt(STRFieldEdit);
                em.getTransaction().begin();
                Medicine med = em.find(Medicine.class, idEdit);
                em.getTransaction().commit();
                if (med != null){
                    InformationEdit.setVisible(false);
                    JToolBar foundPanelEdit = new JToolBar();
                    SpringLayout layout = new SpringLayout();
                    foundPanelEdit.setLayout(layout);
                    foundPanelEdit.setFloatable(false);
                    //Название
                    Component labelName = new JLabel("Название:");
                    foundPanelEdit.add(labelName);
                    layout.putConstraint(SpringLayout.WEST , labelName, 10,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , labelName, 70,
                            SpringLayout.NORTH , foundPanelEdit);

                    JTextArea valueName = new JTextArea(1,10);
                    valueName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    valueName.setText(med.getNameOfMedicine());
                    JScrollPane scrollPaneName = new JScrollPane(valueName);
                    scrollPaneName.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneName, 90,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneName, 70,
                            SpringLayout.NORTH , foundPanelEdit);
                    foundPanelEdit.add(scrollPaneName);
                    //Цена
                    Component labelPrice = new JLabel("Цена:");
                    foundPanelEdit.add(labelPrice);
                    layout.putConstraint(SpringLayout.WEST , labelPrice, 10,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , labelPrice, 90,
                            SpringLayout.NORTH , foundPanelEdit);

                    JTextArea valuePrice = new JTextArea(1,10);
                    valuePrice.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    valuePrice.setText(String.valueOf(med.getPrice()));
                    JScrollPane scrollPanePrice = new JScrollPane(valuePrice);
                    scrollPanePrice.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPanePrice, 90,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPanePrice, 90,
                            SpringLayout.NORTH , foundPanelEdit);
                    foundPanelEdit.add(scrollPanePrice);
                    //Количество
                    Component labelCount = new JLabel("Количество:");
                    foundPanelEdit.add(labelCount);
                    layout.putConstraint(SpringLayout.WEST , labelCount, 10,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , labelCount, 110,
                            SpringLayout.NORTH , foundPanelEdit);

                    JTextArea valueCount = new JTextArea(1,10);
                    valueCount.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    valueCount.setText(String.valueOf(med.getCountOfMedicine()));
                    JScrollPane scrollPaneCount = new JScrollPane(valueCount);
                    scrollPaneCount.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneCount, 90,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneCount, 110,
                            SpringLayout.NORTH , foundPanelEdit);
                    foundPanelEdit.add(scrollPaneCount);

                    JButton FinalButtonEdit = new JButton("Изменить");
                    foundPanelEdit.add(FinalButtonEdit);
                    layout.putConstraint(SpringLayout.WEST , FinalButtonEdit, 90,
                            SpringLayout.WEST , foundPanelEdit);
                    layout.putConstraint(SpringLayout.NORTH , FinalButtonEdit, 140,
                            SpringLayout.NORTH , foundPanelEdit);
                    FoundInformationEdit.add(foundPanelEdit);
                    FoundInformationEdit.setVisible(true);
                    FinalButtonEdit.addActionListener (new ActionListener()
                    {
                        public void actionPerformed (ActionEvent event)
                        {
                            try {
                                logger.debug("Проверка введеных значений информации о лекарстве");
                                checkField(valueName, false, "Название");
                                String textName = valueName.getText();
                                checkField(valuePrice, true, "Цена");
                                String textPrice = valuePrice.getText();
                                checkField(valueCount, true, "Количество");
                                String textCount = valueCount.getText();
                                em.getTransaction().begin();
                                med.setNameOfMedicine(textName);
                                med.setPrice(Integer.parseInt(textPrice));
                                med.setCountOfMedicine(Integer.parseInt(textCount));
                                med.setAvailability(Integer.parseInt(textCount) > 0);
                                em.merge(med);
                                em.getTransaction().commit();
                                FoundInformationEdit.setVisible(false);
                                updateTable();
                                JOptionPane.showMessageDialog(pharmacy, "Информация о лекарстве успешно обновлена");
                            } catch (NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e) {
                                JOptionPane.showMessageDialog(pharmacy, e.getMessage());
                            }
                        }
                    });
                }
                else{
                    throw new NotFoundInDatabase("Лекарство с данным ID не найдено");
                }

            } catch (NumberFormatException | NullPointerException | OnlyLettersException | NotFoundInDatabase| NegativeIntException e1) {
                JOptionPane.showMessageDialog(pharmacy, e1.getMessage());
            }
        });



    }

    /** Метод проверки исключений на ввод данных*/
    private void checkField (JTextArea field, boolean needToCheckNumberFormat, String fieldType) throws NullPointerException, NumberFormatException, OnlyLettersException, NegativeIntException //Метод для проверки на пустое текстовое поле
    {
        logger.debug("Проверка поля: {}", fieldType);
        String textField = field.getText();
        if (textField.isEmpty()) {
            logger.error("Поле '{}' пустое.", fieldType);
            throw new NullPointerException(String.format("Поле '%s' не было заполнено", fieldType));
        }
        if (needToCheckNumberFormat) {
            try {
                Integer.parseInt(textField);
            } catch (NumberFormatException e) {
                logger.error("Поле '{}' содержит некорректное числовое значение: {}", fieldType, textField);
                throw new NumberFormatException(String.format("Неверно введено значение в поле '%s'. Введите числовое значение.", fieldType));
            }
            if (Integer.parseInt(textField)<0){
                logger.error("Значение в поле '{}' отрицательное: {}", fieldType, textField);
                throw new NegativeIntException(String.format("Значение поля '%s' не может быть отрицательным. Введите положительное числовое значение.", fieldType));
            }
        }
        if (!needToCheckNumberFormat) {
            if (!textField.matches("[а-яА-Я]+")) {
                logger.error("Неверный формат данных в поле '{}'. Ожидалось название на кириллице: {}", fieldType, textField);
                throw new OnlyLettersException("Неверно введено значение в поле 'Название'. Введите значение поля с помощью кириллицы.");
            }
            char firstLetter = textField.charAt(0);
            if (!(firstLetter >= 'А' && firstLetter <= 'Я')){
                logger.error("Название поля '{}' должно начинаться с заглавной буквы: {}", fieldType, textField);
                throw new OnlyLettersException("Название лекарства должно начинаться с заглавной буквы.");

            }
        }
    }
    /** Метод обновления таблицы*/
    private void updateTable() {
        logger.info("Обновление таблицы лекарств");
        model.setRowCount(0);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Medicine> cq = cb.createQuery(Medicine.class);
        Root<Medicine> rootEntry = cq.from(Medicine.class);
        List<Order> orderList = new ArrayList<>();
        orderList.add(cb.desc(rootEntry.get("medId")));
        CriteriaQuery<Medicine> all = cq.orderBy(orderList);
        TypedQuery<Medicine> allQuery = em.createQuery(all);
        List<Medicine> meds = allQuery.getResultList();
        // Выводим на экран элементы таблицы
        for (Medicine medDownload : meds) {
            String avail = medDownload.getAvailability() ? "Да":"Нет";
            model.insertRow(0, new Object[]{medDownload.getMedId(), medDownload.getNameOfMedicine(),
                    medDownload.getPrice(), medDownload.getCountOfMedicine(), avail});
        }

    }
    /** Вспомогательный метод для формирования отчета, который использует созданный XML файл таблицы и шаблон iReport*/
    public static void printMethod(String XMLFile, String JRXMLFile, String Result) {
        try {
            logger.debug("Создание отчета и заполнение его информацией из таблицы");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(XMLFile));
            document.getDocumentElement().normalize();
            JasperReport jasperReport = JasperCompileManager.compileReport(JRXMLFile);
            JRXmlDataSource dataSource = new JRXmlDataSource(document, "/medlist/medicine");
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            if (Result.toLowerCase().endsWith("pdf")){
                JasperExportManager.exportReportToPdfFile(jasperPrint, Result);
            } else if (Result.toLowerCase().endsWith("html")) {
                JasperExportManager.exportReportToHtmlFile(jasperPrint, Result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
