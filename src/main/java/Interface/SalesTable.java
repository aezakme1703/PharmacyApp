package Interface;

import MyExceptions.NegativeIntException;
import MyExceptions.NotFoundInDatabase;
import MyExceptions.OnlyLettersException;
import MyExceptions.OtherFormats;
import PharmacyClasses.Medicine;
import PharmacyClasses.Sales;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Класс таблицы с операциями */
public class SalesTable {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test_persistence");
    EntityManager em = emf.createEntityManager();

    private static final Logger logger = LogManager.getLogger(SalesTable.class);

    JFrame salesFrame = new JFrame("Аптека");

    //Создание таблицы
    private static boolean isEditable = false;
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return isEditable;
        }
    };
    JTable salesTable = new JTable(model);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd (HH:mm)");
    /** Метод создания окна с таблицей об операциях*/
    public void show(){
        logger.info("Открытие таблицы с операциями");
        //Создание окна
        salesFrame.setLayout(new BorderLayout());
        salesFrame.setSize(1080, 700);
        salesFrame.setLocation(100, 100);
        salesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        salesFrame.setVisible(true);

        //Создание панели инструментов
        JToolBar toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        SpringLayout layout = new SpringLayout();
        toolBar.setLayout(layout);
        salesFrame.add(toolBar);

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
        JButton sumOfSales = new JButton(new ImageIcon("Icons/dollar.png"));
        JButton reportOfMeds = new JButton(new ImageIcon("Icons/report.png"));

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
        sumOfSales.setToolTipText("Сумма продаж");
        reportOfMeds.setToolTipText("Отчет по продажам");

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
        toolBar.add(updateTable);
        toolBar.add(print);
        layout.putConstraint(SpringLayout.WEST , print, 0,
                SpringLayout.WEST , toolBar);
        layout.putConstraint(SpringLayout.NORTH , print, 1,
                SpringLayout.SOUTH , edit);
        toolBar.add(updateTable);
        layout.putConstraint(SpringLayout.SOUTH , updateTable, 0,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.EAST , updateTable, 0,
                SpringLayout.EAST , toolBar);
        toolBar.add(chooseTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseTable, 0,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseTable, 0,
                SpringLayout.WEST , toolBar);
        toolBar.add(sumOfSales);
        layout.putConstraint(SpringLayout.SOUTH , sumOfSales, -45,
                SpringLayout.SOUTH , chooseTable);
        layout.putConstraint(SpringLayout.WEST , sumOfSales, 0,
                SpringLayout.WEST , toolBar);
        toolBar.add(reportOfMeds);
        layout.putConstraint(SpringLayout.SOUTH , reportOfMeds, -45,
                SpringLayout.SOUTH , sumOfSales);
        layout.putConstraint(SpringLayout.WEST , reportOfMeds, 0,
                SpringLayout.WEST , toolBar);

        salesTable.getTableHeader().setReorderingAllowed(false);
        model.addColumn("ID операции");
        model.addColumn("Дата");
        model.addColumn("Тип операции");
        model.addColumn("Количество");
        model.addColumn("Лекарство");
        updateTable();
        JScrollPane scroll = new JScrollPane(salesTable);
        toolBar.add(scroll);
        layout.putConstraint(SpringLayout.NORTH , scroll, 0,
                SpringLayout.SOUTH , save);
        layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.EAST, save);
        layout.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST, toolBar);
        layout.putConstraint(SpringLayout.SOUTH, scroll, -40, SpringLayout.SOUTH, toolBar);
        layout.putConstraint(SpringLayout.NORTH, scroll, 0, SpringLayout.NORTH, toolBar);
        toolBar.revalidate();
        toolBar.repaint();

        JButton chooseMedTable = new JButton("Лекарства");
        toolBar.add(chooseMedTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseMedTable, -5,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseMedTable, 50,
                SpringLayout.WEST , chooseTable);
        chooseMedTable.setVisible(false);

        JButton chooseDisTable = new JButton("Болезни");
        toolBar.add(chooseDisTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseDisTable, -5,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseDisTable, 73,
                SpringLayout.WEST , chooseMedTable);
        chooseDisTable.setVisible(false);

        final boolean[] isTableVisible = {false};
        chooseTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isTableVisible[0]) {
                    chooseMedTable.setVisible(true);
                    chooseDisTable.setVisible(true);

                } else {
                    // Если таблицы скрыты, показываем их
                    chooseMedTable.setVisible(false);
                    chooseDisTable.setVisible(false);
                }
                isTableVisible[0] = !isTableVisible[0];
            }
        });
        chooseMedTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salesFrame.setVisible(false);
                try {
                    new Application1().Interface();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        chooseDisTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salesFrame.setVisible(false);
                new DiseaseTable().show();
            }
        });

        addInf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonAddInf();
                updateTable();
            }
        });
        deleteInf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonDeleteInf();
            }
        });
        updateTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonEditInf();
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonSave();
            }
        });
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonOpen();
            }
        });
        print.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonPrint();
            }
        });
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonSearchInf();
            }
        });
        sumOfSales.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonSum();
            }
        });
        reportOfMeds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonReportMeds();
            }
        });
    }
    /** Метод для сохранения таблицы с операциями в XML файл*/
    public void ButtonSave() {
        logger.info("Сохранение таблицы операций");
        String fileName = "E:\\Student\\2st course\\OOP\\ReportExample\\Sales.xml";
        try {
            if (fileName.toLowerCase().endsWith(".xml")){
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.newDocument();
                Node saleDoc = doc.createElement("salesList");
                doc.appendChild(saleDoc);
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Sales> cq = cb.createQuery(Sales.class);
                Root<Sales> rootEntry = cq.from(Sales.class);
                java.util.List<Order> orderList = new ArrayList<>();
                orderList.add(cb.desc(rootEntry.get("salesId")));
                CriteriaQuery<Sales> all = cq.orderBy(orderList);
                TypedQuery<Sales> allQuery = em.createQuery(all);
                List<Sales> sales = allQuery.getResultList();
                for (Sales saleDownload : sales) {
                    Element sale = doc.createElement("sale");
                    saleDoc.appendChild(sale);
                    sale.setAttribute("id",String.valueOf(saleDownload.getSalesId()));
                    sale.setAttribute("date",String.valueOf(saleDownload.getDate()));
                    if (!saleDownload.getType()){
                        sale.setAttribute("type","Продажа");
                    }
                    else{
                        sale.setAttribute("type","Поступление");
                    }
                    sale.setAttribute("count",String.valueOf(saleDownload.getCount()));
                    sale.setAttribute("idMed",String.valueOf(saleDownload.getMID().getMedId()));
                }
                Transformer trans = TransformerFactory.newInstance().newTransformer();
                trans.transform(new DOMSource(doc), new StreamResult(writer));
                JOptionPane.showMessageDialog (salesFrame, "Файл успешно сохранен");
            }
            else{
                logger.error("Ошибка при сохранении таблицы операций (неверный формат)");
                throw new OtherFormats("Введен неверный формат. Сохранение данных возможно только в формате XML.");
            }

        }
        catch (ParserConfigurationException | IOException | TransformerException e) { e.printStackTrace(); }
        catch (OtherFormats e) {JOptionPane.showMessageDialog(salesFrame, e.getMessage());}


    }
    /** Метод для открытия и получения данных об операциях из XML файла*/
    public void ButtonOpen() {
        logger.info("Открытие таблицы операций");
        FileDialog open = new FileDialog(salesFrame, "Получение данных", FileDialog.LOAD);
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
                NodeList nlSales = doc.getElementsByTagName("sale");
                boolean anyAdding = false;
                for (int temp = 0; temp < nlSales.getLength(); temp++) {
                    Node elem = nlSales.item(temp);
                    NamedNodeMap attrs = elem.getAttributes();
                    String idMed = attrs.getNamedItem("idMed").getNodeValue();
                    String count = attrs.getNamedItem("count").getNodeValue();
                    String type = attrs.getNamedItem("type").getNodeValue();
                    String date = attrs.getNamedItem("date").getNodeValue();
                    em.getTransaction().begin();
                    Medicine medID = em.find(Medicine.class, Integer.parseInt(idMed));
                    if (medID != null){
                        anyAdding = true;
                        Sales sale = new Sales();
                        sale.setCount(Integer.parseInt(count));
                        sale.setMID(medID);
                        LocalDateTime dateTime = LocalDateTime.parse(date);
                        sale.setDate(dateTime);
                        sale.setType(!type.equals("Продажа"));
                        em.persist(sale);
                        updateTable();
                    }
                    em.getTransaction().commit();
                }

                if (anyAdding){
                    JOptionPane.showMessageDialog (salesFrame, "Новые записи успешно добавлены");
                }
                else{JOptionPane.showMessageDialog (salesFrame, "Новых записей не найдено");}
            } else{
                logger.error("Ошибка при открытии таблицы операций(неверный формат)");
                throw new OtherFormats("Открытие файла возможно только в формате XML");}


        } catch (ParserConfigurationException | SAXException | IOException | OtherFormats e) {
            e.printStackTrace();
        }

    }
    /** Метод для формирования отчета об операциях*/
    public void ButtonPrint() {
        logger.info("Формирование отчета об операциях");
        FileDialog saveReport = new FileDialog(salesFrame, "Формирование отчета", FileDialog.SAVE);
        saveReport.setVisible(true);
        String fileName = saveReport.getDirectory() + saveReport.getFile();
        if (saveReport.getFile() == null) {
            return;
        }
        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                printMethod("E:\\Student\\2st course\\OOP\\ReportExample\\Sales.xml", "E:\\Student\\2st course\\OOP\\ReportExample\\reportSale.jrxml", fileName);
            } else {
                logger.error("Ошибка при формировании отчета об операциях (неверный формат)");
                throw new OtherFormats("Введен неверный формат. Формирование отчета возможно только в формате PDF.");
            }
            JOptionPane.showMessageDialog (salesFrame, "Отчёт успешно сформирован");
        } catch (OtherFormats e) {
            JOptionPane.showMessageDialog(salesFrame, e.getMessage());
        }
    }
    /** Метод для добавления информации об операции*/
    public void ButtonAddInf(){
        logger.info("Добавление информации об операциях");
        JFrame InformationAdd;
        JToolBar panel;
        InformationAdd = new JFrame("Добавление информации");
        InformationAdd.setSize(300, 300);
        InformationAdd.setLocation(1175, 100);
        panel = new JToolBar();
        panel.setFloatable(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10); // верхний, левый, нижний, правый отступ
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // выравнивание по левому краю
        JLabel labelText = new JLabel("Введите ID лекарства");
        panel.add(labelText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER; // выравнивание по левому краю
        JTextArea fieldID = new JTextArea(1,12);
        fieldID.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneID = new JScrollPane(fieldID);
        scrollPaneID.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPaneID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton ButtonNext = new JButton("Далее");
        panel.add(ButtonNext, gbc);

        InformationAdd.add(panel);
        InformationAdd.setVisible(true);

        ButtonNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    logger.debug("Ввод значений информации об операциии");
                    Sales sale1 = new Sales();
                    checkField(fieldID, true, "ID");
                    String saleMID = fieldID.getText();
                    em.getTransaction().begin();
                    Medicine med = em.find(Medicine.class, Integer.parseInt(saleMID));
                    em.getTransaction().commit();
                    if (med != null){
                        sale1.setMID(med);
                    }
                    else{
                        logger.error("Ошибка(лекарство для добавления операции не найдено)");
                        throw new NotFoundInDatabase("Лекарство с данным ID не найдено");}
                    InformationAdd.setSize(420, 300);
                    InformationAdd.setLocation(1050, 100);
                    ButtonNext.setVisible(false);
                    labelText.setVisible(false);
                    scrollPaneID.setVisible(false);

                    SpringLayout layout = new SpringLayout();
                    panel.setLayout(layout);

                    JLabel labelType = new JLabel("Тип операции");
                    layout.putConstraint(SpringLayout.NORTH , labelType, 10,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , labelType, 28,
                            SpringLayout.WEST , panel);
                    panel.add(labelType);

                    JButton operationPlus = new JButton("Поступление");
                    layout.putConstraint(SpringLayout.NORTH , operationPlus, 40,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , operationPlus, 28,
                            SpringLayout.WEST , panel);
                    panel.add(operationPlus);

                    JButton operationMin = new JButton("   Продажа    ");
                    layout.putConstraint(SpringLayout.NORTH , operationMin, 80,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , operationMin, 30,
                            SpringLayout.WEST , panel);
                    panel.add(operationMin);


                    operationPlus.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            sale1.setType(true);
                            operationPlus.setBackground(Color.LIGHT_GRAY);
                            operationMin.setBackground(Color.WHITE);
                        }
                    });

                    operationMin.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            sale1.setType(false);
                            operationMin.setBackground(Color.LIGHT_GRAY);
                            operationPlus.setBackground(Color.WHITE);
                        }
                    });

                    JLabel labelDate = new JLabel("Дата операции");
                    layout.putConstraint(SpringLayout.NORTH , labelDate, 10,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , labelDate, 165,
                            SpringLayout.WEST , panel);
                    panel.add(labelDate);

                    JTextArea fieldDate1 = new JTextArea(1,3);
                    fieldDate1.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate1 = new JScrollPane(fieldDate1);
                    scrollPaneDate1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate1, 45,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate1, 140,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneDate1);

                    JTextArea fieldDate2 = new JTextArea(1,2);
                    fieldDate2.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate2 = new JScrollPane(fieldDate2);
                    scrollPaneDate2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate2, 45,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate2, 175,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneDate2);

                    JTextArea fieldDate3 = new JTextArea(1,2);
                    fieldDate3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate3 = new JScrollPane(fieldDate3);
                    scrollPaneDate3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate3, 45,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate3, 200,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneDate3);

                    JTextArea fieldDate4 = new JTextArea(1,2);
                    fieldDate4.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate4 = new JScrollPane(fieldDate4);
                    scrollPaneDate4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate4, 45,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate4, 230,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneDate4);

                    JTextArea fieldDate5 = new JTextArea(1,2);
                    fieldDate5.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate5 = new JScrollPane(fieldDate5);
                    scrollPaneDate5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate5, 45,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate5, 255,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneDate5);

                    JLabel labelFormat = new JLabel(" 2025  /  6  /  12     12 : 30");
                    labelFormat.setForeground(new Color(41, 39, 39, 121));
                    layout.putConstraint(SpringLayout.NORTH , labelFormat, 65,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , labelFormat, 140,
                            SpringLayout.WEST , panel);
                    panel.add(labelFormat);

                    JButton currentTime = new JButton("Установить текущее время");
                    currentTime.setFont(new Font("Arial", Font.ITALIC, 9));
                    layout.putConstraint(SpringLayout.NORTH , currentTime, 85,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , currentTime, 142,
                            SpringLayout.WEST , panel);
                    panel.add(currentTime);

                    currentTime.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            LocalDateTime time = LocalDateTime.now();
                            fieldDate1.setText(String.valueOf(time.getYear()));
                            fieldDate2.setText(String.valueOf(time.getMonthValue()));
                            fieldDate3.setText(String.valueOf(time.getDayOfMonth()));
                            fieldDate4.setText(String.valueOf(time.getHour()));
                            fieldDate5.setText(String.valueOf(time.getMinute()));
                        }
                    });

                    JLabel labelCount = new JLabel("Количество");
                    layout.putConstraint(SpringLayout.NORTH , labelCount, 10,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , labelCount, 315,
                            SpringLayout.WEST , panel);
                    panel.add(labelCount);
                    JTextArea fieldCount = new JTextArea(1,8);
                    fieldCount.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneCount = new JScrollPane(fieldCount);
                    scrollPaneCount.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneCount, 45,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneCount, 310,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneCount);


                    JButton readyToCreate = new JButton("Готово");
                    layout.putConstraint(SpringLayout.SOUTH , readyToCreate, -20,
                            SpringLayout.SOUTH , panel);
                    layout.putConstraint(SpringLayout.WEST , readyToCreate, 180,
                            SpringLayout.WEST , panel);
                    panel.add(readyToCreate);
                    readyToCreate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ex) {
                            try {
                                logger.debug("Проверка введеных значений об операции");
                                LocalDateTime compareTime = LocalDateTime.now();
                                checkField(fieldDate1, true, "Год");
                                String dateYear = fieldDate1.getText();
                                if (Integer.parseInt(dateYear)<2005){
                                    throw new OtherFormats("Неверно введен год операции.");
                                }
                                checkField(fieldDate2, true, "Месяц");
                                String dateMonth = fieldDate2.getText();
                                if (Integer.parseInt(dateMonth)>12 || Integer.parseInt(dateMonth)<1){
                                    throw new OtherFormats("Неверно введен месяц операции");
                                }
                                checkField(fieldDate3, true, "День");
                                String dateDay = fieldDate3.getText();
                                if (Integer.parseInt(dateDay)>31 || Integer.parseInt(dateDay)<1){
                                    throw new OtherFormats("Неверно введен день операции");
                                }
                                checkField(fieldDate4, true, "Часы");
                                String dateHour = fieldDate4.getText();
                                if (Integer.parseInt(dateHour)>23){
                                    throw new OtherFormats("Неверно введено время операции в часах");
                                }
                                checkField(fieldDate5, true, "Минуты");
                                String dateMinute = fieldDate5.getText();
                                if (Integer.parseInt(dateMinute)>59){
                                    throw new OtherFormats("Неверно введено время операции в минутах");
                                }
                                LocalDateTime operationTime = (LocalDateTime.of(Integer.parseInt(dateYear),Integer.parseInt(dateMonth),
                                        Integer.parseInt(dateDay),Integer.parseInt(dateHour),Integer.parseInt(dateMinute)));
                                if (operationTime.isAfter(compareTime)){
                                    throw new NotFoundInDatabase("Дата операции не может превышать актуальную дату");
                                }
                                sale1.setDate(operationTime);
                                checkField(fieldCount, true, "Количество");
                                String countSale = fieldCount.getText();
                                sale1.setCount(Integer.parseInt(countSale));
                                em.getTransaction().begin();
                                em.persist(sale1);
                                em.getTransaction().commit();
                                InformationAdd.setVisible(false);
                                updateTable();
                                JOptionPane.showMessageDialog(salesFrame, "Операция успешно добавлена");
                            } catch (NullPointerException | NumberFormatException | OtherFormats | OnlyLettersException | NegativeIntException | NotFoundInDatabase e) {
                                JOptionPane.showMessageDialog(salesFrame, e.getMessage());
                            }
                        }
                    });
                }catch (NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException | NotFoundInDatabase e1) {
                    JOptionPane.showMessageDialog(salesFrame, e1.getMessage());}
            }
        });
    }
    /** Метод для удаления информации об операции*/
    public void ButtonDeleteInf(){
        logger.info("Удаление информации об операции");
        JFrame InformationDelete;
        JButton ButtonDelete;
        JToolBar panelDelete;
        InformationDelete = new JFrame("Удаление информации");
        InformationDelete.setSize(300, 300);
        InformationDelete.setLocation(1150, 100);
        panelDelete = new JToolBar();
        panelDelete.setLayout(new GridBagLayout());
        panelDelete.setFloatable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        InformationDelete.add(panelDelete);
        InformationDelete.setVisible(true);
        gbc.insets = new Insets(10, 10, 10, 10); // верхний, левый, нижний, правый отступ

        gbc.gridx = 0; // колонка
        gbc.gridy = 4; // строка
        gbc.anchor = GridBagConstraints.CENTER;
        panelDelete.add(new JLabel("Введите ID операции"), gbc);
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
        salesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                if (row != -1) {
                    Object idValue = salesTable.getValueAt(row, 0);
                    fieldDelete.setText(idValue.toString());
                }
            }
        });

        ButtonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    logger.debug("Проверка введенного ID операции");
                    checkField(fieldDelete, true, "ID");
                    String STRFieldDelete = fieldDelete.getText();
                    int idDelete = Integer.parseInt(STRFieldDelete);
                    em.getTransaction().begin();
                    Sales sale1 = em.find(Sales.class, idDelete);
                    if (sale1 != null){
                        int option = JOptionPane.showConfirmDialog(
                                null,
                                "Вы уверены что хотите удалить выбранную операцию?",
                                "Подтверждение удаления",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );
                        if (option == JOptionPane.YES_OPTION){
                            logger.debug("Удаление операции из таблицы");
                            em.remove(sale1);
                            em.getTransaction().commit();
                            updateTable();
                            InformationDelete.setVisible(false);
                            JOptionPane.showMessageDialog(salesFrame, "Операция удалена успешно");
                        } else{
                            em.getTransaction().commit();
                            InformationDelete.setVisible(false);
                        }

                    }
                    else{
                        em.getTransaction().commit();
                        fieldDelete.setText(null);
                        throw new NotFoundInDatabase("Операция с данным ID не найдена");
                    }

                } catch (NumberFormatException | NotFoundInDatabase | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                    JOptionPane.showMessageDialog(salesFrame, e1.getMessage());
                }
            }
        });


    }
    /** Метод для поиска информации об операции*/
    public void ButtonSearchInf(){
        logger.info("Поиск информаци об операции");
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

        JLabel labelSearch = new JLabel("Введите ID операции");
        panelSearch.add(labelSearch);
        layout.putConstraint(SpringLayout.NORTH , labelSearch, 80,
                SpringLayout.NORTH , panelSearch);
        layout.putConstraint(SpringLayout.WEST , labelSearch, 105,
                SpringLayout.WEST , panelSearch);

        JTextArea fieldSearchID = new JTextArea(1,15);
        fieldSearchID.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneID = new JScrollPane(fieldSearchID);
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

        salesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                if (row != -1) {
                    Object idValue = salesTable.getValueAt(row, 0);
                    fieldSearchID.setText(idValue.toString());
                }
            }
        });

        nextSearch.addActionListener(e -> {
            try {
                logger.debug("Вывод найденных значений информации об операции");
                checkField(fieldSearchID, true, "ID");
                String saleID = fieldSearchID.getText();
                em.getTransaction().begin();
                Sales sale = em.find(Sales.class, Integer.parseInt(saleID));
                em.getTransaction().commit();
                if (sale != null){
                    frameSearch.setSize(300, 250);
                    frameSearch.setLocation(1050, 100);
                    fieldSearchID.setVisible(false);
                    scrollPaneID.setVisible(false);
                    labelSearch.setVisible(false);
                    nextSearch.setVisible(false);

                    JLabel labelType = new JLabel("Тип операции: ");
                    layout.putConstraint(SpringLayout.NORTH , labelType, 20,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , labelType, 50,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(labelType);

                    JLabel fieldType = new JLabel();

                    fieldType.setForeground(new Color(27, 175, 62, 255));
                    if (sale.getType()){
                        fieldType.setText("Поступление");
                    } else{fieldType.setText("Продажа");}
                    layout.putConstraint(SpringLayout.NORTH , fieldType, 20,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , fieldType, 150,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(fieldType);



                    JLabel labelDate = new JLabel("Дата операции:");
                    layout.putConstraint(SpringLayout.NORTH , labelDate, 50,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , labelDate, 50,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(labelDate);

                    String date = sale.getDate().format(formatter);
                    JLabel fieldDate = new JLabel(date);
                    fieldDate.setForeground(new Color(27, 175, 62, 255));
                    layout.putConstraint(SpringLayout.NORTH , fieldDate, 50,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , fieldDate, 150,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(fieldDate);

                    JLabel labelCount = new JLabel("Количество: ");
                    layout.putConstraint(SpringLayout.NORTH , labelCount, 80,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , labelCount, 50,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(labelCount);

                    JLabel fieldCount = new JLabel(String.valueOf(sale.getCount()));
                    fieldCount.setForeground(new Color(27, 175, 62, 255));
                    layout.putConstraint(SpringLayout.NORTH , fieldCount, 80,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , fieldCount, 150,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(fieldCount);

                    JLabel labelMed = new JLabel("Лекарство: ");
                    layout.putConstraint(SpringLayout.NORTH , labelMed, 110,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , labelMed, 50,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(labelMed);

                    JLabel fieldMed = new JLabel(String.valueOf(sale.getMID().getNameOfMedicine()));
                    fieldMed.setForeground(new Color(27, 175, 62, 255));
                    layout.putConstraint(SpringLayout.NORTH , fieldMed, 110,
                            SpringLayout.NORTH , panelSearch);
                    layout.putConstraint(SpringLayout.WEST , fieldMed, 150,
                            SpringLayout.WEST , panelSearch);
                    panelSearch.add(fieldMed);

                }
                else{
                    throw new NotFoundInDatabase("Операция с данным ID не найдена");
                }

            } catch (NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                JOptionPane.showMessageDialog(salesFrame, e1.getMessage());
            }
        });

    }
    /** Метод для редактирования информации об операции*/
    public void ButtonEditInf(){
        logger.info("Редактирования информации об операции");
        JFrame InformationEdit;
        JButton foundEditID = new JButton("Найти");;
        JToolBar panelEdit;
        InformationEdit = new JFrame("Редактирование информации");
        InformationEdit.setSize(350, 300);
        InformationEdit.setLocation(1175, 100);

        panelEdit = new JToolBar();
        panelEdit.setFloatable(false);
        panelEdit.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel labelID = new JLabel("Введите ID операции для редактирования");
        panelEdit.add(labelID, gbc);
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea fieldEdit = new JTextArea(1,20);
        fieldEdit.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        salesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                if (row != -1) {
                    Object idValue = salesTable.getValueAt(row, 0);
                    fieldEdit.setText(idValue.toString());
                }
            }
        });
        JScrollPane scrollPaneID = new JScrollPane(fieldEdit);
        scrollPaneID.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelEdit.add(scrollPaneID, gbc);

        gbc.gridy = 50;
        gbc.anchor = GridBagConstraints.CENTER;

        panelEdit.add(foundEditID, gbc);
        InformationEdit.add(panelEdit);
        InformationEdit.setVisible(true);

        foundEditID.addActionListener(e -> {
            try {
                checkField(fieldEdit, true, "ID");
                String STRFieldEdit = fieldEdit.getText();
                int idFound = Integer.parseInt(STRFieldEdit);
                em.getTransaction().begin();
                Sales sale = em.find(Sales.class, idFound);
                em.getTransaction().commit();
                if (sale != null){
                    InformationEdit.setSize(420, 300);
                    InformationEdit.setLocation(1050, 100);
                    scrollPaneID.setVisible(false);
                    labelID.setVisible(false);
                    foundEditID.setVisible(false);
                    SpringLayout layout = new SpringLayout();
                    panelEdit.setLayout(layout);

                    JLabel labelType = new JLabel("Тип операции");
                    layout.putConstraint(SpringLayout.NORTH , labelType, 10,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , labelType, 28,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(labelType);

                    JButton operationPlus = new JButton("Поступление");
                    layout.putConstraint(SpringLayout.NORTH , operationPlus, 40,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , operationPlus, 28,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(operationPlus);

                    JButton operationMin = new JButton("   Продажа    ");
                    layout.putConstraint(SpringLayout.NORTH , operationMin, 80,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , operationMin, 30,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(operationMin);

                    if (sale.getType()){
                        operationPlus.doClick();
                    }
                    else{
                        operationMin.doClick();
                    }

                    operationPlus.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            sale.setType(true);
                            operationPlus.setBackground(Color.LIGHT_GRAY);
                            operationMin.setBackground(Color.WHITE);
                        }
                    });

                    operationMin.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            sale.setType(false);
                            operationMin.setBackground(Color.LIGHT_GRAY);
                            operationPlus.setBackground(Color.WHITE);
                        }
                    });

                    JLabel labelDate = new JLabel("Дата операции");
                    layout.putConstraint(SpringLayout.NORTH , labelDate, 10,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , labelDate, 165,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(labelDate);

                    JTextArea fieldDate1 = new JTextArea(1,3);
                    fieldDate1.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate1 = new JScrollPane(fieldDate1);
                    scrollPaneDate1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate1, 45,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate1, 140,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneDate1);

                    JTextArea fieldDate2 = new JTextArea(1,2);
                    fieldDate2.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate2 = new JScrollPane(fieldDate2);
                    scrollPaneDate2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate2, 45,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate2, 175,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneDate2);

                    JTextArea fieldDate3 = new JTextArea(1,2);
                    fieldDate3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate3 = new JScrollPane(fieldDate3);
                    scrollPaneDate3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate3, 45,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate3, 200,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneDate3);

                    JTextArea fieldDate4 = new JTextArea(1,2);
                    fieldDate4.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate4 = new JScrollPane(fieldDate4);
                    scrollPaneDate4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate4, 45,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate4, 230,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneDate4);

                    JTextArea fieldDate5 = new JTextArea(1,2);
                    fieldDate5.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneDate5 = new JScrollPane(fieldDate5);
                    scrollPaneDate5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneDate5, 45,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneDate5, 255,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneDate5);

                    JLabel labelFormat = new JLabel(" 2025  /  6  /  12     12 : 30");
                    labelFormat.setForeground(new Color(41, 39, 39, 121));
                    layout.putConstraint(SpringLayout.NORTH , labelFormat, 65,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , labelFormat, 140,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(labelFormat);

                    JButton currentTime = new JButton("Установить текущее время");
                    currentTime.setFont(new Font("Arial", Font.ITALIC, 9));
                    layout.putConstraint(SpringLayout.NORTH , currentTime, 85,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , currentTime, 142,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(currentTime);

                    currentTime.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            LocalDateTime time = LocalDateTime.now();
                            fieldDate1.setText(String.valueOf(time.getYear()));
                            fieldDate2.setText(String.valueOf(time.getMonthValue()));
                            fieldDate3.setText(String.valueOf(time.getDayOfMonth()));
                            fieldDate4.setText(String.valueOf(time.getHour()));
                            fieldDate5.setText(String.valueOf(time.getMinute()));
                        }
                    });

                    JLabel labelCount = new JLabel("Количество");
                    layout.putConstraint(SpringLayout.NORTH , labelCount, 10,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , labelCount, 315,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(labelCount);
                    JTextArea fieldCount = new JTextArea(1,8);
                    fieldCount.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneCount = new JScrollPane(fieldCount);
                    scrollPaneCount.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneCount, 45,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneCount, 310,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneCount);

                    JLabel labelMID = new JLabel("ID лекарства");
                    layout.putConstraint(SpringLayout.NORTH , labelMID, 125,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , labelMID, 170,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(labelMID);

                    JTextArea fieldIDmed = new JTextArea(1,8);
                    fieldIDmed.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneIDmed = new JScrollPane(fieldIDmed);
                    scrollPaneIDmed.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneIDmed, 150,
                            SpringLayout.NORTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneIDmed, 167,
                            SpringLayout.WEST , panelEdit);
                    panelEdit.add(scrollPaneIDmed);
                    fieldIDmed.setText(String.valueOf(sale.getMID().getMedId()));

                    fieldDate1.setText(String.valueOf(sale.getDate().getYear()));
                    fieldDate2.setText(String.valueOf(sale.getDate().getMonthValue()));
                    fieldDate3.setText(String.valueOf(sale.getDate().getDayOfMonth()));
                    fieldDate4.setText(String.valueOf(sale.getDate().getHour()));
                    fieldDate5.setText(String.valueOf(sale.getDate().getMinute()));

                    fieldCount.setText(String.valueOf(sale.getCount()));
                    JButton readyToChange = new JButton("Изменить");
                    panelEdit.add(readyToChange);
                    layout.putConstraint(SpringLayout.SOUTH , readyToChange, -20,
                            SpringLayout.SOUTH , panelEdit);
                    layout.putConstraint(SpringLayout.WEST , readyToChange, 180,
                            SpringLayout.WEST , panelEdit);

                    readyToChange.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ex) {
                            try {
                                logger.debug("Проверка введеных значений информации об операции");
                                checkField(fieldIDmed,true, "ID");
                                int idMed = Integer.parseInt(fieldIDmed.getText());
                                em.getTransaction().begin();
                                Medicine med = em.find(Medicine.class, idMed);
                                em.getTransaction().commit();
                                if (med != null){
                                    sale.setMID(med);
                                }
                                else{throw new NotFoundInDatabase("Лекарство с данным ID не найдено");}

                                LocalDateTime compareTime = LocalDateTime.now();
                                String dateYear = fieldDate1.getText();
                                if (Integer.parseInt(dateYear)<2005){
                                    throw new OtherFormats("Неверно введен год операции.");
                                }
                                checkField(fieldDate1, true, "Год");
                                String dateMonth = fieldDate2.getText();
                                if (Integer.parseInt(dateMonth)>12 || Integer.parseInt(dateMonth)<1){
                                    throw new OtherFormats("Неверно введен месяц операции");
                                }
                                checkField(fieldDate2, true, "Месяц");
                                String dateDay = fieldDate3.getText();
                                if (Integer.parseInt(dateDay)>31 || Integer.parseInt(dateDay)<1){
                                    throw new OtherFormats("Неверно введен день операции");
                                }
                                checkField(fieldDate3, true, "День");
                                String dateHour = fieldDate4.getText();
                                if (Integer.parseInt(dateHour)>23){
                                    throw new OtherFormats("Неверно введено время операции в часах");
                                }
                                checkField(fieldDate4, true, "Часы");
                                String dateMinute = fieldDate5.getText();
                                if (Integer.parseInt(dateMinute)>59){
                                    throw new OtherFormats("Неверно введено время операции в минутах");
                                }
                                checkField(fieldDate5, true, "Минуты");
                                LocalDateTime operationTime = (LocalDateTime.of(Integer.parseInt(dateYear),Integer.parseInt(dateMonth),
                                        Integer.parseInt(dateDay),Integer.parseInt(dateHour),Integer.parseInt(dateMinute)));
                                if (!operationTime.isAfter(compareTime)){
                                    sale.setDate(operationTime);
                                }
                                else{throw new NotFoundInDatabase("Дата операции не может превышать актуальную дату");}

                                checkField(fieldCount, true, "Количество");
                                String countSale = fieldCount.getText();
                                sale.setCount(Integer.parseInt(countSale));
                                em.getTransaction().begin();
                                logger.debug("Изменении информации об операции в таблице");
                                em.merge(sale);
                                em.getTransaction().commit();
                                InformationEdit.setVisible(false);
                                updateTable();
                                JOptionPane.showMessageDialog(salesFrame, "Информация об операции успешно обновлена");

                            } catch (NumberFormatException | OtherFormats | OnlyLettersException | NegativeIntException | NotFoundInDatabase e) {
                                JOptionPane.showMessageDialog(salesFrame, e.getMessage());
                            }
                        }
                    });

                }
                else{
                    throw new NotFoundInDatabase("Операция с данным ID не найдена");
                }

            } catch (NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                JOptionPane.showMessageDialog(salesFrame, e1.getMessage());
            }
        });



    }
    /** Метод для подсчета суммы продаж лекарств за указанный период времени*/
    public void ButtonSum(){
        logger.info("Нахождение суммы продаж за выбранный период");
        JFrame frameSum;
        frameSum = new JFrame("Сумма продаж");
        frameSum.setSize(500, 300);
        frameSum.setLocation(900, 100);
        frameSum.setVisible(true);

        JToolBar panelSum = new JToolBar();
        frameSum.add(panelSum);
        SpringLayout layout = new SpringLayout();
        panelSum.setLayout(layout);
        panelSum.setFloatable(false);

        JLabel textLabel = new JLabel("Выберете временной промежуток для подсчёта продаж");
        layout.putConstraint(SpringLayout.NORTH , textLabel, 40,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , textLabel, 65,
                SpringLayout.WEST , panelSum);
        panelSum.add(textLabel);

        JTextArea fieldDate1 = new JTextArea(1,3);
        fieldDate1.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate1 = new JScrollPane(fieldDate1);
        scrollPaneDate1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate1, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate1, 280,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate1);

        JTextArea fieldDate2 = new JTextArea(1,2);
        fieldDate2.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate2 = new JScrollPane(fieldDate2);
        scrollPaneDate2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate2, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate2, 315,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate2);

        JTextArea fieldDate3 = new JTextArea(1,2);
        fieldDate3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate3 = new JScrollPane(fieldDate3);
        scrollPaneDate3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate3, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate3, 340,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate3);

        JTextArea fieldDate4 = new JTextArea(1,2);
        fieldDate4.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate4 = new JScrollPane(fieldDate4);
        scrollPaneDate4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate4, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate4, 370,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate4);

        JTextArea fieldDate5 = new JTextArea(1,2);
        fieldDate5.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate5 = new JScrollPane(fieldDate5);
        scrollPaneDate5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate5, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate5, 395,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate5);

        JTextArea fieldDate11 = new JTextArea(1,3);
        fieldDate11.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate11 = new JScrollPane(fieldDate11);
        scrollPaneDate11.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate11, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate11, 40,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate11);


        JTextArea fieldDate21 = new JTextArea(1,2);
        fieldDate21.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate21 = new JScrollPane(fieldDate21);
        scrollPaneDate21.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate21, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate21, 75,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate21);

        JTextArea fieldDate31 = new JTextArea(1,2);
        fieldDate31.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate31 = new JScrollPane(fieldDate31);
        scrollPaneDate31.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate31, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate31, 100,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate31);

        JTextArea fieldDate41 = new JTextArea(1,2);
        fieldDate41.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate41 = new JScrollPane(fieldDate41);
        scrollPaneDate41.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate41, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate41, 130,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate41);

        JTextArea fieldDate51 = new JTextArea(1,2);
        fieldDate51.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate51 = new JScrollPane(fieldDate51);
        scrollPaneDate51.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate51, 90,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate51, 155,
                SpringLayout.WEST , panelSum);
        panelSum.add(scrollPaneDate51);

        JLabel labelFormat = new JLabel(" 2025  /  6  /  12     12 : 30");
        labelFormat.setForeground(new Color(41, 39, 39, 121));
        layout.putConstraint(SpringLayout.NORTH , labelFormat, 110,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , labelFormat, 40,
                SpringLayout.WEST , panelSum);
        panelSum.add(labelFormat);

        JLabel textOt = new JLabel("От");
        textOt.setForeground(new Color(41, 39, 39, 121));
        layout.putConstraint(SpringLayout.NORTH , textOt, 65,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , textOt, 100,
                SpringLayout.WEST , panelSum);
        panelSum.add(textOt);

        JLabel textDo = new JLabel("До");
        textDo.setForeground(new Color(41, 39, 39, 121));
        layout.putConstraint(SpringLayout.NORTH , textDo, 65,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , textDo, 342,
                SpringLayout.WEST , panelSum);
        panelSum.add(textDo);

        JButton currentTime = new JButton("Установить текущее время");
        currentTime.setFont(new Font("Arial", Font.ITALIC, 9));
        layout.putConstraint(SpringLayout.NORTH , currentTime, 120,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , currentTime, 282,
                SpringLayout.WEST , panelSum);
        panelSum.add(currentTime);

        JButton countMoney = new JButton("Рассчитать");
        layout.putConstraint(SpringLayout.NORTH , countMoney, 180,
                SpringLayout.NORTH , panelSum);
        layout.putConstraint(SpringLayout.WEST , countMoney, 190,
                SpringLayout.WEST , panelSum);
        panelSum.add(countMoney);

        currentTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime time = LocalDateTime.now();
                fieldDate1.setText(String.valueOf(time.getYear()));
                fieldDate2.setText(String.valueOf(time.getMonthValue()));
                fieldDate3.setText(String.valueOf(time.getDayOfMonth()));
                fieldDate4.setText(String.valueOf(time.getHour()));
                fieldDate5.setText(String.valueOf(time.getMinute()));
            }
        });
        countMoney.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    logger.info("Проверка введеных значений дат");
                    LocalDateTime nowTime = LocalDateTime.now();
                    checkField(fieldDate11, true, "Год (От)");
                    String dateYear1 = fieldDate11.getText();
                    if (Integer.parseInt(dateYear1)>nowTime.getYear()){
                        throw new OtherFormats("Неверно введен год операции (От)");
                    }
                    checkField(fieldDate21, true, "Месяц (От)");
                    String dateMonth1 = fieldDate21.getText();
                    if (Integer.parseInt(dateMonth1)>12 || Integer.parseInt(dateMonth1)<1){
                        throw new OtherFormats("Неверно введен месяц операции (От)");
                    }
                    checkField(fieldDate31, true, "День (От)");
                    String dateDay1 = fieldDate31.getText();
                    if (Integer.parseInt(dateDay1)>31 || Integer.parseInt(dateDay1)<1){
                        throw new OtherFormats("Неверно введен день операции (От)");
                    }
                    checkField(fieldDate41, true, "Часы (От)");
                    String dateHour1 = fieldDate41.getText();
                    if (Integer.parseInt(dateHour1)>23){
                        throw new OtherFormats("Неверно введено время операции в часах (От)");
                    }
                    checkField(fieldDate51, true, "Минуты (От)");
                    String dateMinute1 = fieldDate51.getText();
                    if (Integer.parseInt(dateMinute1)>59){
                        throw new OtherFormats("Неверно введено время операции в минутах (От)");
                    }
                    LocalDateTime operationTimeOt = (LocalDateTime.of(Integer.parseInt(dateYear1),Integer.parseInt(dateMonth1),
                            Integer.parseInt(dateDay1),Integer.parseInt(dateHour1),Integer.parseInt(dateMinute1)));

                    checkField(fieldDate1, true, "Год (До)");
                    String dateYear = fieldDate1.getText();
                    if (Integer.parseInt(dateYear)>nowTime.getYear()){
                        throw new OtherFormats("Неверно введен год операции (До)");
                    }
                    checkField(fieldDate2, true, "Месяц (До)");
                    String dateMonth = fieldDate2.getText();
                    if (Integer.parseInt(dateMonth)>12 || Integer.parseInt(dateMonth)<1){
                        throw new OtherFormats("Неверно введен месяц операции (До)");
                    }
                    checkField(fieldDate3, true, "День (До)");
                    String dateDay = fieldDate3.getText();
                    if (Integer.parseInt(dateDay)>31 || Integer.parseInt(dateDay)<1){
                        throw new OtherFormats("Неверно введен день операции (До)");
                    }
                    checkField(fieldDate4, true, "Часы (До)");
                    String dateHour = fieldDate4.getText();
                    if (Integer.parseInt(dateHour)>23){
                        throw new OtherFormats("Неверно введено время операции в часах (До)");
                    }
                    checkField(fieldDate5, true, "Минуты (До)");
                    String dateMinute = fieldDate5.getText();
                    if (Integer.parseInt(dateMinute)>59){
                        throw new OtherFormats("Неверно введено время операции в минутах (До)");
                    }
                    LocalDateTime operationTimeDo = (LocalDateTime.of(Integer.parseInt(dateYear),Integer.parseInt(dateMonth),
                            Integer.parseInt(dateDay),Integer.parseInt(dateHour),Integer.parseInt(dateMinute)));

                    if (operationTimeDo.isAfter(nowTime)){
                        throw new NotFoundInDatabase("Дата операции не может превышать актуальную дату");
                    }
                    if (operationTimeOt.isAfter(nowTime)){
                        throw new NotFoundInDatabase("Дата операции не может превышать актуальную дату");
                    }
                    int sumOfMoney = 0;
                    em.getTransaction().begin();
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Sales> cq = cb.createQuery(Sales.class);
                    Root<Sales> rootEntry = cq.from(Sales.class);
                    java.util.List<Order> orderList = new ArrayList<>();
                    orderList.add(cb.desc(rootEntry.get("salesId")));
                    CriteriaQuery<Sales> all = cq.orderBy(orderList);
                    TypedQuery<Sales> allQuery = em.createQuery(all);
                    List<Sales> sales = allQuery.getResultList();
                    for (Sales saleDownload : sales) {
                        if (saleDownload.getDate().isAfter(operationTimeOt) &&
                                saleDownload.getDate().isBefore(operationTimeDo) && !saleDownload.getType()){
                            sumOfMoney += (saleDownload.getMID().getPrice() * saleDownload.getCount());
                        }
                    }
                    if (sumOfMoney != 0){
                        logger.info("Вывод суммы продаж за выбранный период");
                        frameSum.setVisible(false);
                        JOptionPane.showMessageDialog(salesFrame,"Сумма продаж за выбранный период: " + sumOfMoney);
                    }
                    else{
                        frameSum.setVisible(false);
                        JOptionPane.showMessageDialog(salesFrame,"За выбранный период продажи не найдены");
                    }

                } catch (NumberFormatException | NullPointerException | OtherFormats | OnlyLettersException | NegativeIntException | NotFoundInDatabase e1) {
                    JOptionPane.showMessageDialog(salesFrame, e1.getMessage());
                } finally {
                    em.getTransaction().commit();
                }
            }

        });

    }
    /** Метод для формирования отчета о проданных лекарствах за указанный период времени*/
    public void ButtonReportMeds(){
        logger.info("Отчет по продажам лекарств");
        JFrame frameReport = new JFrame("Отчет по продажам");
        frameReport.setSize(500, 300);
        frameReport.setLocation(900, 100);
        frameReport.setVisible(true);

        JToolBar panelReport = new JToolBar();
        SpringLayout layout = new SpringLayout();
        panelReport.setLayout(layout);
        frameReport.add(panelReport);
        panelReport.setFloatable(false);

        JLabel textLabel = new JLabel("Выберете временной промежуток");
        layout.putConstraint(SpringLayout.NORTH , textLabel, 30,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , textLabel, 130,
                SpringLayout.WEST , panelReport);
        panelReport.add(textLabel);

        JTextArea fieldDate1 = new JTextArea(1,3);
        fieldDate1.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate1 = new JScrollPane(fieldDate1);
        scrollPaneDate1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate1, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate1, 280,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate1);

        JTextArea fieldDate2 = new JTextArea(1,2);
        fieldDate2.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate2 = new JScrollPane(fieldDate2);
        scrollPaneDate2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate2, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate2, 315,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate2);

        JTextArea fieldDate3 = new JTextArea(1,2);
        fieldDate3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate3 = new JScrollPane(fieldDate3);
        scrollPaneDate3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate3, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate3, 340,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate3);

        JTextArea fieldDate4 = new JTextArea(1,2);
        fieldDate4.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate4 = new JScrollPane(fieldDate4);
        scrollPaneDate4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate4, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate4, 370,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate4);

        JTextArea fieldDate5 = new JTextArea(1,2);
        fieldDate5.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate5 = new JScrollPane(fieldDate5);
        scrollPaneDate5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate5, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate5, 395,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate5);

        JTextArea fieldDate11 = new JTextArea(1,3);
        fieldDate11.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate11 = new JScrollPane(fieldDate11);
        scrollPaneDate11.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate11, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate11, 40,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate11);


        JTextArea fieldDate21 = new JTextArea(1,2);
        fieldDate21.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate21 = new JScrollPane(fieldDate21);
        scrollPaneDate21.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate21, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate21, 75,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate21);

        JTextArea fieldDate31 = new JTextArea(1,2);
        fieldDate31.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate31 = new JScrollPane(fieldDate31);
        scrollPaneDate31.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate31, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate31, 100,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate31);

        JTextArea fieldDate41 = new JTextArea(1,2);
        fieldDate41.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate41 = new JScrollPane(fieldDate41);
        scrollPaneDate41.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate41, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate41, 130,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate41);

        JTextArea fieldDate51 = new JTextArea(1,2);
        fieldDate51.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneDate51 = new JScrollPane(fieldDate51);
        scrollPaneDate51.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        layout.putConstraint(SpringLayout.NORTH , scrollPaneDate51, 90,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , scrollPaneDate51, 155,
                SpringLayout.WEST , panelReport);
        panelReport.add(scrollPaneDate51);

        JLabel labelFormat = new JLabel(" 2025  /  6  /  12     12 : 30");
        labelFormat.setForeground(new Color(41, 39, 39, 121));
        layout.putConstraint(SpringLayout.NORTH , labelFormat, 110,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , labelFormat, 40,
                SpringLayout.WEST , panelReport);
        panelReport.add(labelFormat);

        JLabel textOt = new JLabel("От");
        textOt.setForeground(new Color(41, 39, 39, 121));
        layout.putConstraint(SpringLayout.NORTH , textOt, 65,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , textOt, 100,
                SpringLayout.WEST , panelReport);
        panelReport.add(textOt);

        JLabel textDo = new JLabel("До");
        textDo.setForeground(new Color(41, 39, 39, 121));
        layout.putConstraint(SpringLayout.NORTH , textDo, 65,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , textDo, 342,
                SpringLayout.WEST , panelReport);
        panelReport.add(textDo);

        JButton currentTime = new JButton("Установить текущее время");
        currentTime.setFont(new Font("Arial", Font.ITALIC, 9));
        layout.putConstraint(SpringLayout.NORTH , currentTime, 120,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , currentTime, 282,
                SpringLayout.WEST , panelReport);
        panelReport.add(currentTime);

        JButton createTable = new JButton("Сформировать таблицу");
        layout.putConstraint(SpringLayout.NORTH , createTable, 180,
                SpringLayout.NORTH , panelReport);
        layout.putConstraint(SpringLayout.WEST , createTable, 190,
                SpringLayout.WEST , panelReport);
        panelReport.add(createTable);

        currentTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime time = LocalDateTime.now();
                fieldDate1.setText(String.valueOf(time.getYear()));
                fieldDate2.setText(String.valueOf(time.getMonthValue()));
                fieldDate3.setText(String.valueOf(time.getDayOfMonth()));
                fieldDate4.setText(String.valueOf(time.getHour()));
                fieldDate5.setText(String.valueOf(time.getMinute()));
            }
        });


        DefaultTableModel modelReport = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditable;
            }
        };
        JTable reportTable = new JTable(modelReport);
        modelReport.addColumn("Название лекарства");
        modelReport.addColumn("Количество продаж");

        createTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    logger.info("Проверка введеных значений дат для формирования отчета по продажам");
                    LocalDateTime nowTime = LocalDateTime.now();
                    checkField(fieldDate11, true, "Год (От)");
                    String dateYear1 = fieldDate11.getText();
                    if (Integer.parseInt(dateYear1)>nowTime.getYear()){
                        throw new OtherFormats("Неверно введен год операции (От)");
                    }
                    checkField(fieldDate21, true, "Месяц (От)");
                    String dateMonth1 = fieldDate21.getText();
                    if (Integer.parseInt(dateMonth1)>12 || Integer.parseInt(dateMonth1)<1){
                        throw new OtherFormats("Неверно введен месяц операции (От)");
                    }
                    checkField(fieldDate31, true, "День (От)");
                    String dateDay1 = fieldDate31.getText();
                    if (Integer.parseInt(dateDay1)>31 || Integer.parseInt(dateDay1)<1){
                        throw new OtherFormats("Неверно введен день операции (От)");
                    }
                    checkField(fieldDate41, true, "Часы (От)");
                    String dateHour1 = fieldDate41.getText();
                    if (Integer.parseInt(dateHour1)>23){
                        throw new OtherFormats("Неверно введено время операции в часах (От)");
                    }
                    checkField(fieldDate51, true, "Минуты (От)");
                    String dateMinute1 = fieldDate51.getText();
                    if (Integer.parseInt(dateMinute1)>59){
                        throw new OtherFormats("Неверно введено время операции в минутах (От)");
                    }
                    LocalDateTime operationTimeOt = (LocalDateTime.of(Integer.parseInt(dateYear1),Integer.parseInt(dateMonth1),
                            Integer.parseInt(dateDay1),Integer.parseInt(dateHour1),Integer.parseInt(dateMinute1)));

                    checkField(fieldDate1, true, "Год (До)");
                    String dateYear = fieldDate1.getText();
                    if (Integer.parseInt(dateYear)>nowTime.getYear()){
                        throw new OtherFormats("Неверно введен год операции (До)");
                    }
                    checkField(fieldDate2, true, "Месяц (До)");
                    String dateMonth = fieldDate2.getText();
                    if (Integer.parseInt(dateMonth)>12 || Integer.parseInt(dateMonth)<1){
                        throw new OtherFormats("Неверно введен месяц операции (До)");
                    }
                    checkField(fieldDate3, true, "День (До)");
                    String dateDay = fieldDate3.getText();
                    if (Integer.parseInt(dateDay)>31 || Integer.parseInt(dateDay)<1){
                        throw new OtherFormats("Неверно введен день операции (До)");
                    }
                    checkField(fieldDate4, true, "Часы (До)");
                    String dateHour = fieldDate4.getText();
                    if (Integer.parseInt(dateHour)>23){
                        throw new OtherFormats("Неверно введено время операции в часах (До)");
                    }
                    checkField(fieldDate5, true, "Минуты (До)");
                    String dateMinute = fieldDate5.getText();
                    if (Integer.parseInt(dateMinute)>59){
                        throw new OtherFormats("Неверно введено время операции в минутах (До)");
                    }
                    LocalDateTime operationTimeDo = (LocalDateTime.of(Integer.parseInt(dateYear),Integer.parseInt(dateMonth),
                            Integer.parseInt(dateDay),Integer.parseInt(dateHour),Integer.parseInt(dateMinute)));

                    if (operationTimeDo.isAfter(nowTime)){
                        throw new NotFoundInDatabase("Дата операции не может превышать актуальную дату");
                    }
                    if (operationTimeOt.isAfter(nowTime)){
                        throw new NotFoundInDatabase("Дата операции не может превышать актуальную дату");
                    }
                    panelReport.setVisible(false);

                    JToolBar newPanelReport = new JToolBar();
                    SpringLayout layout1 = new SpringLayout();
                    newPanelReport.setLayout(layout1);
                    frameReport.add(newPanelReport);
                    newPanelReport.setFloatable(false);

                    JLabel timePart = new JLabel();
                    String timeOt = operationTimeOt.format(formatter);
                    String timeDo = operationTimeDo.format(formatter);
                    timePart.setText(timeOt + " - " + timeDo);
                    newPanelReport.add(timePart);
                    layout1.putConstraint(SpringLayout.WEST, timePart, 140, SpringLayout.WEST, newPanelReport);
                    layout1.putConstraint(SpringLayout.NORTH, timePart, 5, SpringLayout.NORTH, newPanelReport);

                    JScrollPane scrollReport = new JScrollPane(reportTable);
                    newPanelReport.add(scrollReport);
                    layout1.putConstraint(SpringLayout.WEST, scrollReport, 15, SpringLayout.WEST, newPanelReport);
                    layout1.putConstraint(SpringLayout.NORTH, scrollReport, 30, SpringLayout.NORTH, newPanelReport);
                    layout1.putConstraint(SpringLayout.SOUTH, scrollReport, -1, SpringLayout.SOUTH, newPanelReport);
                    newPanelReport.revalidate();
                    newPanelReport.repaint();

                    em.getTransaction().begin();
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Sales> cq = cb.createQuery(Sales.class);
                    Root<Sales> rootEntry = cq.from(Sales.class);
                    java.util.List<Order> orderList = new ArrayList<>();
                    orderList.add(cb.desc(rootEntry.get("salesId")));
                    CriteriaQuery<Sales> all = cq.orderBy(orderList);
                    TypedQuery<Sales> allQuery = em.createQuery(all);
                    List<Sales> sales = allQuery.getResultList();

                    Map<String, Integer> medicineCountMap = new HashMap<>();
                    for (Sales saleDownload : sales) {
                        if (saleDownload.getDate().isAfter(operationTimeOt) &&
                                saleDownload.getDate().isBefore(operationTimeDo) && !saleDownload.getType()) {
                            String medicineName = saleDownload.getMID().getNameOfMedicine();
                            int currentCount = saleDownload.getCount();
                            medicineCountMap.put(medicineName, medicineCountMap.getOrDefault(medicineName, 0) + currentCount);
                        }
                    }
                    for (Map.Entry<String, Integer> entry : medicineCountMap.entrySet()) {
                        modelReport.insertRow(0, new Object[]{entry.getKey(), entry.getValue()});
                    }
                    em.getTransaction().commit();

                } catch (NumberFormatException | NullPointerException | OtherFormats | OnlyLettersException | NegativeIntException | NotFoundInDatabase e1) {
                    JOptionPane.showMessageDialog(salesFrame, e1.getMessage());
                }
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
        logger.info("Обновление таблицы операций");
        model.setRowCount(0);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sales> cq = cb.createQuery(Sales.class);
        Root<Sales> rootEntry = cq.from(Sales.class);
        java.util.List<Order> orderList = new ArrayList<>();
        orderList.add(cb.desc(rootEntry.get("salesId")));
        CriteriaQuery<Sales> all = cq.orderBy(orderList);
        TypedQuery<Sales> allQuery = em.createQuery(all);
        List<Sales> sales = allQuery.getResultList();
        // Выводим на экран элементы таблицы
        for (Sales saleDownload : sales) {
            String type = saleDownload.getType() ? "Поступление":"Продажа";
            model.insertRow(0, new Object[]{saleDownload.getSalesId(),saleDownload.getDate().format(formatter),type,saleDownload.getCount(),
                    (saleDownload.getMID().getNameOfMedicine() + "(" + saleDownload.getMID().getMedId() + ")")});
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
            JRXmlDataSource dataSource = new JRXmlDataSource(document, "/salesList/sale");
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
