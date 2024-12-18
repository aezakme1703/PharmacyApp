package Interface;

import MyExceptions.NegativeIntException;
import MyExceptions.NotFoundInDatabase;
import MyExceptions.OnlyLettersException;
import MyExceptions.OtherFormats;
import PharmacyClasses.Disease;
import PharmacyClasses.Medicine;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
/** Класс таблицы с болезнями */
public class DiseaseTable {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test_persistence");
    EntityManager em = emf.createEntityManager();
    private static final Logger logger = LogManager.getLogger(DiseaseTable.class);

    JFrame disFrame = new JFrame("Аптека");
    private static boolean isEditable = false;
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return isEditable;
        }
    };
    JTable disTable = new JTable(model);
    /** Метод создания окна с таблицей о болезнях*/
    public void show(){
        logger.info("Открытие таблицы с болезнями");
        disFrame.setLayout(new BorderLayout());
        disFrame.setSize(1080, 700);
        disFrame.setLocation(100, 100);
        disFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        disFrame.setVisible(true);

        //Создание панели инструментов
        JToolBar toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        SpringLayout layout = new SpringLayout();
        toolBar.setLayout(layout);
        disFrame.add(toolBar);

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

        JButton chooseMedTable = new JButton("Лекарства");
        toolBar.add(chooseMedTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseMedTable, -5,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseMedTable, 50,
                SpringLayout.WEST , chooseTable);
        chooseMedTable.setVisible(false);

        JButton chooseSaleTable = new JButton("Операции");
        toolBar.add(chooseSaleTable);
        layout.putConstraint(SpringLayout.SOUTH , chooseSaleTable, -5,
                SpringLayout.SOUTH , toolBar);
        layout.putConstraint(SpringLayout.WEST , chooseSaleTable, 73,
                SpringLayout.WEST , chooseMedTable);
        chooseSaleTable.setVisible(false);

        disTable.getTableHeader().setReorderingAllowed(false);
        model.addColumn("ID болезни");
        model.addColumn("Название");
        model.addColumn("Названия лекарств");
        updateTable();
        JScrollPane scroll = new JScrollPane(disTable);
        toolBar.add(scroll);
        layout.putConstraint(SpringLayout.NORTH , scroll, 0,
                SpringLayout.SOUTH , save);
        layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.EAST, save);
        layout.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST, toolBar);
        layout.putConstraint(SpringLayout.SOUTH, scroll, -40, SpringLayout.SOUTH, toolBar);
        layout.putConstraint(SpringLayout.NORTH, scroll, 0, SpringLayout.NORTH, toolBar);
        toolBar.revalidate();
        toolBar.repaint();
        final boolean[] isTableVisible = {false};
        chooseTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isTableVisible[0]) {
                    chooseMedTable.setVisible(true);
                    chooseSaleTable.setVisible(true);

                } else {
                    // Если таблицы скрыты, показываем их
                    chooseMedTable.setVisible(false);
                    chooseSaleTable.setVisible(false);
                }
                isTableVisible[0] = !isTableVisible[0];
            }
        });
        chooseMedTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disFrame.setVisible(false);
                try {
                    new Application1().Interface();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        chooseSaleTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disFrame.setVisible(false);
                new SalesTable().show();
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
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonEditInf();
            }
        });
        updateTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
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

    }
    /** Метод для сохранения таблицы с болезнями в XML файл*/
    public void ButtonSave() {
        logger.info("Сохранение таблицы болезней");
        String fileName = "E:\\Student\\2st course\\OOP\\ReportExample\\Diseases.xml";
        try {
            if (fileName.toLowerCase().endsWith(".xml")){
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.newDocument();
                Node disDoc = doc.createElement("disList");
                doc.appendChild(disDoc);
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Disease> cq = cb.createQuery(Disease.class);
                Root<Disease> rootEntry = cq.from(Disease.class);
                java.util.List<Order> orderList = new ArrayList<>();
                orderList.add(cb.desc(rootEntry.get("disID")));
                CriteriaQuery<Disease> all = cq.orderBy(orderList);
                TypedQuery<Disease> allQuery = em.createQuery(all);
                List<Disease> diseases = allQuery.getResultList();
                for (Disease disDownload : diseases) {
                    Element disease = doc.createElement("disease");
                    disDoc.appendChild(disease);
                    disease.setAttribute("id",  String.valueOf(disDownload.getDisId()));
                    disease.setAttribute("name", String.valueOf(disDownload.getNameOfDisease()));
                    disease.setAttribute("idMed", String.valueOf(disDownload.getMed().stream()
                            .filter(Objects::nonNull)
                            .map(currentMedicine -> String.valueOf(currentMedicine.getNameOfMedicine()))
                            .collect(Collectors.joining(", "))));
                }
                Transformer trans = TransformerFactory.newInstance().newTransformer();
                trans.transform(new DOMSource(doc), new StreamResult(writer));
                JOptionPane.showMessageDialog (disFrame, "Файл успешно сохранен.");
            }
            else{
                logger.error("Ошибка при сохранении таблицы болезней (неверный формат)");
                throw new OtherFormats("Введен неверный формат. Сохранение данных возможно только в формате XML.");
            }

        }
        catch (ParserConfigurationException | IOException | TransformerException e) { e.printStackTrace(); }
        catch (OtherFormats e) {JOptionPane.showMessageDialog(disFrame, e.getMessage());}


    }
    /** Метод для открытия и получения данных о болезнях из XML файла*/
    public void ButtonOpen() {
        logger.info("Открытие таблицы болезней");
        FileDialog open = new FileDialog(disFrame, "Получение данных", FileDialog.LOAD);
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
                NodeList nlDis = doc.getElementsByTagName("disease");

                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Disease> cq = cb.createQuery(Disease.class);
                Root<Disease> rootEntry = cq.from(Disease.class);
                List<Order> orderList = new ArrayList<>();
                orderList.add(cb.desc(rootEntry.get("disID")));
                CriteriaQuery<Disease> all = cq.orderBy(orderList);
                TypedQuery<Disease> allQuery = em.createQuery(all);
                List<Disease> diseases = allQuery.getResultList();
                boolean anyAdding = false;
                for (int temp = 0; temp < nlDis.getLength(); temp++) {
                    Node elem = nlDis.item(temp);
                    NamedNodeMap attrs = elem.getAttributes();
                    String name = attrs.getNamedItem("name").getNodeValue();
                    em.getTransaction().begin();
                    boolean needToAddMed = true;

                    for (Disease disDownload : diseases) {
                        if (disDownload.getNameOfDisease().equals(name)){
                            needToAddMed = false;
                            break;
                        }
                    }
                    if (needToAddMed) {
                        anyAdding = true;
                        Disease dis = new Disease();
                        dis.setNameOfDisease(name);
                        em.persist(dis);
                        updateTable();
                    }
                    em.getTransaction().commit();
                }

                if (anyAdding){
                    JOptionPane.showMessageDialog (disFrame, "Новые записи успешно добавлены");
                }
                else{JOptionPane.showMessageDialog (disFrame, "Новых записей не найдено");}
            } else{
                logger.error("Ошибка при открытии таблицы болезней(неверный формат)");
                throw new OtherFormats("Введен неверный формат. Сохранение данных возможно только в формате XML.");}


        } catch (ParserConfigurationException | SAXException | IOException | OtherFormats e) {
            e.printStackTrace();
        }

    }
    /** Метод для формирования отчета о болезнях*/
    public void ButtonPrint() {
        logger.info("Формирование отчета о болезнях");
        FileDialog saveReport = new FileDialog(disFrame, "Формирование отчета", FileDialog.SAVE);
        saveReport.setVisible(true);
        String fileName = saveReport.getDirectory() + saveReport.getFile();
        if (saveReport.getFile() == null) {
            return;
        }
        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                printMethod("E:\\Student\\2st course\\OOP\\ReportExample\\Diseases.xml", "E:\\Student\\2st course\\OOP\\ReportExample\\reportDis.jrxml", fileName);
            } else {
                logger.error("Ошибка при формировании отчета о болезнях (неверный формат)");
                throw new OtherFormats("Введен неверный формат. Формирование отчета возможно только в формате PDF.");
            }
            JOptionPane.showMessageDialog (disFrame, "Отчёт успешно сформирован");
        } catch (OtherFormats e) {
            JOptionPane.showMessageDialog(disFrame, e.getMessage());
        }
    }
    /** Метод для добавления информации о болезни*/
    public void ButtonAddInf(){
        logger.info("Добавление информации о болезнях");
        JFrame InformationAdd;
        JToolBar panel;
        InformationAdd = new JFrame("Добавление информации");
        InformationAdd.setSize(300, 300);
        InformationAdd.setLocation(1175, 100);
        panel = new JToolBar();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setFloatable(false);

        gbc.insets = new Insets(10, 10, 10, 10); // верхний, левый, нижний, правый отступ
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // выравнивание по левому краю
        JLabel labelText = new JLabel("Введите название болезни");
        panel.add(labelText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER; // выравнивание по левому краю
        JTextArea fieldName = new JTextArea(1,12);
        fieldName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        JScrollPane scrollPaneName = new JScrollPane(fieldName);
        scrollPaneName.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPaneName, gbc);

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
                    checkField(fieldName, false, "Название");
                    String disName = fieldName.getText();
                    ButtonNext.setVisible(false);
                    labelText.setVisible(false);
                    scrollPaneName.setVisible(false);
                    SpringLayout layout = new SpringLayout();
                    panel.setLayout(layout);

                    JLabel labelText1 = new JLabel("Введите ID лекарств");
                    layout.putConstraint(SpringLayout.NORTH , labelText1, 20,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , labelText1, 70,
                            SpringLayout.WEST , panel);
                    panel.add(labelText1);

                    JTextArea fieldID1 = new JTextArea(1,12);
                    fieldID1.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneID1 = new JScrollPane(fieldID1);
                    scrollPaneID1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneID1, 60,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneID1, 10,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneID1);

                    JButton deleteID1 = new JButton(new ImageIcon("Icons/deleteID.png"));
                    layout.putConstraint(SpringLayout.NORTH , deleteID1, 58,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , deleteID1, 145,
                            SpringLayout.WEST , panel);
                    panel.add(deleteID1);

                    deleteID1.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            fieldID1.setText(null);
                        }
                    });
                    JTextArea fieldID2 = new JTextArea(1,12);
                    fieldID2.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneID2 = new JScrollPane(fieldID2);
                    scrollPaneID2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneID2, 90,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneID2, 10,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneID2);

                    JButton deleteID2 = new JButton(new ImageIcon("Icons/deleteID.png"));
                    layout.putConstraint(SpringLayout.NORTH , deleteID2, 88,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , deleteID2, 145,
                            SpringLayout.WEST , panel);
                    panel.add(deleteID2);
                    deleteID2.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            fieldID2.setText(null);
                        }
                    });

                    JTextArea fieldID3 = new JTextArea(1,12);
                    fieldID3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
                    JScrollPane scrollPaneID3 = new JScrollPane(fieldID3);
                    scrollPaneID3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneID3, 120,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneID3, 10,
                            SpringLayout.WEST , panel);
                    panel.add(scrollPaneID3);

                    JButton deleteID3 = new JButton(new ImageIcon("Icons/deleteID.png"));
                    layout.putConstraint(SpringLayout.NORTH , deleteID3, 118,
                            SpringLayout.NORTH , panel);
                    layout.putConstraint(SpringLayout.WEST , deleteID3, 145,
                            SpringLayout.WEST , panel);
                    panel.add(deleteID3);
                    deleteID3.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            fieldID3.setText(null);
                        }
                    });

                    JButton readyToCreate = new JButton("Готово");
                    layout.putConstraint(SpringLayout.SOUTH , readyToCreate, -20,
                            SpringLayout.SOUTH , panel);
                    layout.putConstraint(SpringLayout.WEST , readyToCreate, 110,
                            SpringLayout.WEST , panel);
                    panel.add(readyToCreate);
                    readyToCreate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ex) {
                            try {
                                logger.info("Проверка введеных значений о болезни");
                                em.getTransaction().begin();
                                Disease disease = new Disease();
                                disease.setNameOfDisease(disName);
                                String medID1 = fieldID1.getText();
                                if (!medID1.isEmpty()){
                                    checkField(fieldID1, true, "ID");
                                    Medicine medicine1 = em.find(Medicine.class, Integer.parseInt(medID1));
                                    if (medicine1 != null){
                                        disease.getMed().add(medicine1);
                                    }
                                    else{
                                        em.getTransaction().commit();
                                        throw new NotFoundInDatabase("Лекарство с данным ID в 1ом поле не найдено");}
                                }

                                String medID2 = fieldID2.getText();
                                if (!medID2.isEmpty()){
                                    checkField(fieldID2, true, "ID");
                                    Medicine medicine2 = em.find(Medicine.class, Integer.parseInt(medID2));
                                    if (medicine2 != null){
                                        disease.getMed().add(medicine2);
                                    }else{
                                        em.getTransaction().commit();
                                        throw new NotFoundInDatabase("Лекарство с данным ID во 2ом поле не найдено");}
                                }

                                String medID3 = fieldID3.getText();
                                if (!medID3.isEmpty()){
                                    checkField(fieldID3, true, "ID");
                                    Medicine medicine3 = em.find(Medicine.class, Integer.parseInt(medID3));
                                    if (medicine3 != null){
                                        disease.getMed().add(medicine3);
                                    }else{
                                        em.getTransaction().commit();
                                        throw new NotFoundInDatabase("Лекарство с данным ID в 3ем поле не найдено");}
                                }
                                em.persist(disease);
                                em.getTransaction().commit();
                                InformationAdd.setVisible(false);
                                updateTable();
                                JOptionPane.showMessageDialog(disFrame, "Болезнь успешно добавлена");
                            } catch (NumberFormatException | OnlyLettersException | NegativeIntException | NotFoundInDatabase e) {
                                JOptionPane.showMessageDialog(disFrame, e.getMessage());
                            }
                        }
                    });
                }catch (NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                    JOptionPane.showMessageDialog(disFrame, e1.getMessage());}
            }
        });

    }
    /** Метод для удаления информации о болезни*/
    public void ButtonDeleteInf(){
        logger.info("Удаление информации о болезни");
        JFrame InformationDelete;
        JButton ButtonDelete;
        JToolBar panelDelete;

        InformationDelete = new JFrame("Удаление информации");
        InformationDelete.setSize(300, 300);
        InformationDelete.setLocation(1150, 100);
        panelDelete = new JToolBar();
        panelDelete.setLayout(new GridBagLayout());
        panelDelete.setFloatable(false);
        InformationDelete.add(panelDelete);
        InformationDelete.setVisible(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panelDelete.add(new JLabel("Введите ID болезни"), gbc);
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
        disTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = disTable.getSelectedRow(); // Получаем индекс выбранной строки
                if (row != -1) { // Проверяем, что строка выбрана
                    Object idValue = disTable.getValueAt(row, 0); // Получаем значение ID (первая колонка)
                    fieldDelete.setText(idValue.toString()); // Устанавливаем значение в поле
                }
            }
        });

        ButtonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    logger.debug("Проверка введенного ID болезни");
                    checkField(fieldDelete, true, "ID");
                    String STRFieldDelete = fieldDelete.getText();
                    int idDelete = Integer.parseInt(STRFieldDelete);
                    em.getTransaction().begin();
                    Disease dis = em.find(Disease.class, idDelete);
                    if (dis != null){
                        int option = JOptionPane.showConfirmDialog(
                                null,
                                "Вы уверены что хотите удалить выбранную болезнь?",
                                "Подтверждение удаления",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );
                        if (option == JOptionPane.YES_OPTION){
                            logger.debug("Удаление болезни из таблицы");
                            em.remove(dis);
                            em.getTransaction().commit();
                            InformationDelete.setVisible(false);
                            updateTable();
                            JOptionPane.showMessageDialog(disFrame, "Болезнь удалена успешно");
                        } else{
                            em.getTransaction().commit();
                            InformationDelete.setVisible(false);
                        }

                    }
                    else{
                        em.getTransaction().commit();
                        fieldDelete.setText(null);
                        throw new NotFoundInDatabase("Болезнь с данным ID не найдена");
                    }

                } catch (NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                    JOptionPane.showMessageDialog(disFrame, e1.getMessage());
                }
            }
        });
    }
    /** Метод для поиска информации о болезни*/
    public void ButtonSearchInf(){
        logger.info("Поиск информаци о болезни");
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

        JLabel labelSearch = new JLabel("Введите название болезни");
        panelSearch.add(labelSearch);
        layout.putConstraint(SpringLayout.NORTH , labelSearch, 80,
                SpringLayout.NORTH , panelSearch);
        layout.putConstraint(SpringLayout.WEST , labelSearch, 90,
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

        disTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = disTable.getSelectedRow();
                if (row != -1) {
                    Object idValue = disTable.getValueAt(row, 1);
                    fieldSearchName.setText(idValue.toString());
                }
            }
        });

        nextSearch.addActionListener(e -> {
            try {
                logger.debug("Вывод найденных значений информации о болезни");
                checkField(fieldSearchName, false, "Название");
                String disName = fieldSearchName.getText();
                Disease dis = new Disease();

                em.getTransaction().begin();
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Disease> cq = cb.createQuery(Disease.class);
                Root<Disease> rootEntry = cq.from(Disease.class);
                List<Order> orderList = new ArrayList<>();
                orderList.add(cb.desc(rootEntry.get("disID")));
                CriteriaQuery<Disease> all = cq.orderBy(orderList);
                TypedQuery<Disease> allQuery = em.createQuery(all);
                List<Disease> diseases = allQuery.getResultList();
                boolean addNew = false;
                for (Disease disDownload : diseases) {
                    if (disName.equals(disDownload.getNameOfDisease())){
                        dis = em.find(Disease.class, disDownload.getDisId());
                        em.getTransaction().commit();
                        addNew = true;
                        break;
                    }
                }
                if (!addNew){
                    em.getTransaction().commit();
                    throw new NotFoundInDatabase("Болезнь с введённым названием не найдена");
                }
                frameSearch.setSize(400, 250);
                frameSearch.setLocation(1050, 100);
                fieldSearchName.setVisible(false);
                scrollPaneID.setVisible(false);
                labelSearch.setVisible(false);
                nextSearch.setVisible(false);

                JLabel labelName = new JLabel("Название болезни:");
                layout.putConstraint(SpringLayout.NORTH , labelName, 50,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelName, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelName);

                JLabel fieldName = new JLabel();
                fieldName.setText(dis.getNameOfDisease());
                fieldName.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldName, 50,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldName, 165,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldName);

                JLabel labelMed = new JLabel("Названия лекарств:");
                layout.putConstraint(SpringLayout.NORTH , labelMed, 110,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , labelMed, 50,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(labelMed);

                JLabel fieldMed = new JLabel(String.valueOf(dis.getMed().stream()
                        .filter(Objects::nonNull)
                        .map(currentMedicine -> String.valueOf(currentMedicine.getNameOfMedicine()))
                        .collect(Collectors.joining(", "))));
                fieldMed.setForeground(new Color(27, 175, 62, 255));
                layout.putConstraint(SpringLayout.NORTH , fieldMed, 110,
                        SpringLayout.NORTH , panelSearch);
                layout.putConstraint(SpringLayout.WEST , fieldMed, 168,
                        SpringLayout.WEST , panelSearch);
                panelSearch.add(fieldMed);

            } catch (NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                JOptionPane.showMessageDialog(disFrame, e1.getMessage());
            }
        });

    }
    /** Метод для редактирования информации о болезни*/
    public void ButtonEditInf(){
        logger.info("Редактирования информации о болезни");
        JFrame InformationEdit;
        JButton foundEditID = new JButton("Найти");
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
        JLabel labelID = new JLabel("Введите ID болезни для редактирования");
        panelEdit.add(labelID, gbc);
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea fieldEdit = new JTextArea(1,20);
        disTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = disTable.getSelectedRow();
                if (row != -1) {
                    Object idValue = disTable.getValueAt(row, 0);
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
                em.getTransaction().begin();
                checkField(fieldEdit, true, "ID");
                String STRFieldEdit = fieldEdit.getText();
                int idDelete = Integer.parseInt(STRFieldEdit);
                Disease dis = em.find(Disease.class, idDelete);
                em.getTransaction().commit();
                if (dis != null){
                    scrollPaneID.setVisible(false);
                    labelID.setVisible(false);
                    foundEditID.setVisible(false);

                    SpringLayout layout = new SpringLayout();
                    panelEdit.setLayout(layout);

                    Component labelName = new JLabel("Название");
                    panelEdit.add(labelName);
                    layout.putConstraint(SpringLayout.WEST , labelName, 58,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , labelName, 70,
                            SpringLayout.NORTH , panelEdit);

                    JTextArea valueName = new JTextArea(1,10);
                    valueName.setText(dis.getNameOfDisease());
                    JScrollPane scrollPaneName = new JScrollPane(valueName);
                    scrollPaneName.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneName, 35,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneName, 90,
                            SpringLayout.NORTH , panelEdit);
                    panelEdit.add(scrollPaneName);

                    Component labelMedID = new JLabel("ID лекарств");
                    panelEdit.add(labelMedID);
                    layout.putConstraint(SpringLayout.WEST , labelMedID, 196,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , labelMedID, 70,
                            SpringLayout.NORTH , panelEdit);

                    JTextArea valueID1 = new JTextArea(1, 10);
                    JScrollPane scrollPaneID1 = new JScrollPane(valueID1);
                    scrollPaneID1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneID1, 180,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneID1, 90,
                            SpringLayout.NORTH , panelEdit);
                    panelEdit.add(scrollPaneID1);

                    JTextArea valueID2 = new JTextArea(1, 10);
                    JScrollPane scrollPaneID2 = new JScrollPane(valueID2);
                    scrollPaneID2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneID2, 180,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneID2, 110,
                            SpringLayout.NORTH , panelEdit);
                    panelEdit.add(scrollPaneID2);

                    JTextArea valueID3 = new JTextArea(1, 10);
                    JScrollPane scrollPaneID3 = new JScrollPane(valueID3);
                    scrollPaneID3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    layout.putConstraint(SpringLayout.WEST , scrollPaneID3, 180,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , scrollPaneID3, 130,
                            SpringLayout.NORTH , panelEdit);
                    panelEdit.add(scrollPaneID3);

                    JButton FinalButtonEdit = new JButton("Изменить");
                    panelEdit.add(FinalButtonEdit);
                    layout.putConstraint(SpringLayout.WEST , FinalButtonEdit, 130,
                            SpringLayout.WEST , panelEdit);
                    layout.putConstraint(SpringLayout.NORTH , FinalButtonEdit, 200,
                            SpringLayout.NORTH , panelEdit);

                    FinalButtonEdit.addActionListener (new ActionListener()
                    {
                        public void actionPerformed (ActionEvent event)
                        {
                            try {
                                logger.debug("Проверка введеных значений информации о болезни");
                                checkField(valueName, false, "Название");
                                String disName = valueName.getText();
                                em.getTransaction().begin();
                                logger.debug("Изменении информации о болезни в таблице");
                                dis.setNameOfDisease(disName);
                                dis.getMed().clear();
                                em.merge(dis);
                                em.getTransaction().commit();

                                if (!valueID1.getText().isEmpty()){
                                    checkField(valueID1, true, "ID-1");
                                    em.getTransaction().begin();
                                    Medicine med = em.find(Medicine.class, Integer.parseInt(valueID1.getText()));
                                    em.getTransaction().commit();
                                    if (med != null){
                                        dis.getMed().add(med);
                                    } else{
                                        throw new NotFoundInDatabase("Лекарство с данным ID в 1ом поле не найдено");
                                    }
                                }
                                if (!valueID2.getText().isEmpty()){
                                    if (!Objects.equals(valueID2.getText(), valueID1.getText())){
                                        checkField(valueID2, true, "ID-2");
                                        em.getTransaction().begin();
                                        Medicine med = em.find(Medicine.class, Integer.parseInt(valueID2.getText()));
                                        em.getTransaction().commit();
                                        if (med != null){
                                            dis.getMed().add(med);
                                        } else{
                                            throw new NotFoundInDatabase("Лекарство с данным ID во 2ом поле не найдено");
                                        }
                                    }
                                    else{
                                        throw new NotFoundInDatabase("Значения ID лекарств не могут повторяться");
                                    }
                                }
                                if (!valueID3.getText().isEmpty()){
                                    if (!Objects.equals(valueID3.getText(), valueID2.getText()) &&
                                            !Objects.equals(valueID3.getText(), valueID1.getText()) ){
                                        checkField(valueID3, true, "ID-3");
                                        em.getTransaction().begin();
                                        Medicine med = em.find(Medicine.class, Integer.parseInt(valueID3.getText()));
                                        em.getTransaction().commit();
                                        if (med != null){
                                            dis.getMed().add(med);
                                        } else{
                                            throw new NotFoundInDatabase("Лекарство с данным ID в 3ем поле не найдено");
                                        }
                                    } else{
                                        throw new NotFoundInDatabase("Значения ID лекарств не могут повторяться");
                                    }
                                }
                                em.getTransaction().begin();
                                em.merge(dis);
                                em.getTransaction().commit();
                                InformationEdit.setVisible(false);
                                updateTable();
                                JOptionPane.showMessageDialog(disFrame, "Информация о болезни успешно обновлена");
                            } catch (NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException | NotFoundInDatabase  e) {
                                JOptionPane.showMessageDialog(disFrame, e.getMessage());
                            }
                        }
                    });
                }
                else{
                    throw new NotFoundInDatabase("Болезнь с данным ID не найдена");
                }

            } catch (NotFoundInDatabase | NumberFormatException | NullPointerException | OnlyLettersException | NegativeIntException e1) {
                JOptionPane.showMessageDialog(disFrame, e1.getMessage());}
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
        logger.info("Обновление таблицы болезней");
        model.setRowCount(0);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Disease> cq = cb.createQuery(Disease.class);
        Root<Disease> rootEntry = cq.from(Disease.class);
        java.util.List<Order> orderList = new ArrayList<>();
        orderList.add(cb.desc(rootEntry.get("disID")));
        CriteriaQuery<Disease> all = cq.orderBy(orderList);
        TypedQuery<Disease> allQuery = em.createQuery(all);
        List<Disease> diseases = allQuery.getResultList();
        // Выводим на экран элементы таблицы
        for (Disease disDownload : diseases) {
            model.insertRow(0, new Object[]{disDownload.getDisId(), disDownload.getNameOfDisease(),
                    disDownload.getMed().stream()
                            .filter(Objects::nonNull)
                            .map(currentMedicine -> String.valueOf(currentMedicine.getNameOfMedicine()))
                            .collect(Collectors.joining(", "))});
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
            JRXmlDataSource dataSource = new JRXmlDataSource(document, "/disList/disease");
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
