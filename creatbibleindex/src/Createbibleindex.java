import java.awt.*;  
import java.awt.event.*; 

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.lang.reflect.*;


import ru.bible.*;

import org.apache.lucene.morphology.russian.RussianAnalyzer;


@SuppressWarnings("serial")
class Createbibleindex  extends JPanel implements ActionListener  {
	
@SuppressWarnings("rawtypes")
public static 	Class [] classParm = null;
public static	Object [] objectParm = null;
public static Object[] objectParm2 = null;
public static File INDEX_DIR = new File("./index_rubible");

//выбираем нужный язык Библии
//public static String lang = "zh";

//для создания индекса в оперативной памяти - плюс 10 секунд перед стартом - пока не надо
//Directory index = new RAMDirectory();

//Directory index = FSDirectory.open(new File(INDEX_DIR)); 
Directory index = FSDirectory.open(INDEX_DIR); 

RussianAnalyzer analyzer = new RussianAnalyzer();

//RussianAnalyzer analyzer  = new RussianAnalyzer(Version.LUCENE_35);

public static String markup = "";

	
	  JLabel jlabWC; 
	  
	  JEditorPane jta; 
	  
	  JScrollPane jscrlp;
	  
	  
	  /*
	  String name = "ru.bible.rubible" + ibook;
		Class cl = Class.forName(name);
		java.lang.reflect.Constructor co = cl.getConstructor(classParm);
		co.newInstance(objectParm);	
	  */
	  
	  //String classproperties = lang + "biblenames";
	  
	  //public static Integer counterbible;

	  public static JComboBox jcbb;
	  static {
		  jcbb = new JComboBox(rubibleproperties.rubiblenames);
	  }
	  
	  public static JComboBox glav;
	  
	  public static JComboBox searchhits;
	  
	  JTextField searchfield;
	  
	  JButton searchbutton;
	  
	  
	  Createbibleindex() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		  
    	JFrame jfrm = new JFrame("Библия"); 
   	 
        jfrm.getContentPane().setLayout(new FlowLayout()); 
     
        jfrm.setSize(500, 520); 
        
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        jta = new JEditorPane("text/html", markup); 

        jta.setMargin(new Insets(10,10,5,5));
        
        jlabWC = new JLabel("выдач: ");

        jscrlp = new JScrollPane(jta); 
        jscrlp.setPreferredSize(new Dimension(480, 420)); 
        
        glav = new JComboBox();
        searchhits = new JComboBox();
        searchhits.addItem("50");
        searchhits.addItem("200");
        searchhits.addItem("500");
        searchhits.addItem("1000");
        searchhits.addItem("5000");
        
        searchbutton = new JButton("Искать");
        searchfield = new JTextField(26);

        jcbb.setActionCommand("biblebook");
        glav.setActionCommand("glavi");
        this.searchbutton.setActionCommand("actionsearch");
        
        jcbb.addActionListener(this);
        glav.addActionListener(this);

        searchbutton.addActionListener(this);
     
        searchfield.setPreferredSize(new Dimension(80,26));
        jcbb.setPreferredSize(new Dimension(300,25));

           
        jfrm.getContentPane().add(jcbb); 
        jfrm.getContentPane().add(glav); 
        jfrm.getContentPane().add(jscrlp);
        jfrm.getContentPane().add(jlabWC);
        jfrm.getContentPane().add(searchhits); 
        jfrm.getContentPane().add(searchfield);
        jfrm.getContentPane().add(searchbutton);
        
        

        jfrm.setResizable(false);
        
        jfrm.setMaximumSize(null);
        
        jfrm.setVisible(true); 
        
        
        jfrm.setLocationRelativeTo(null);
        
        //обычный анализатор - без учета русской морфологии - не используем
        //analyzer = new StandardAnalyzer(Version.LUCENE_35);
        
        biblesettext(1, 1, "1");

		jta.setEditable(false);
		ToolTipManager.sharedInstance().registerComponent(jta);
		HyperlinkListener l = new HyperlinkListener() {
	        @Override
	        public void hyperlinkUpdate(HyperlinkEvent e) {
	            if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
	                //jta.setPage(e.getURL());
					try {
						String biblrstring = e.getURL().toString();
						String[] urlarray = biblrstring.split("_");
						Integer intbible = Integer.parseInt(urlarray[1]);
						Integer intchapter = Integer.parseInt(urlarray[2]);
						String strpoem = urlarray[3];
						try {
							biblesettext(intbible,intchapter,strpoem);
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchMethodException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InstantiationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }

	        }

	    };
	    jta.addHyperlinkListener(l);
        
        //вызываем этот метод только, если нужной в первый раз создать индекс
	    //createbibleindex();
		
	}
	  
	  /*
	  private void createbibleindex() throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException  {
		  
		//создаем индекс на диске (или в памяти)
	        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
	        IndexWriter writer = new IndexWriter(index, config);


	        	//String biblenamefield = "biblename";
	        	String biblefield = "bible";
            	String chapterfield = "chapter";
            	String poemfield = "poem";
            	String poemtextfield = "poemtext";

            	//забираем в память всю Библию для индексирования
            	for(int ibook = 1; ibook<=66; ibook++) {

            	//counterbible = ibook;
            		
        		String name = "ru.bible.rubible" + ibook;
        		Class cl = Class.forName(name);
        		java.lang.reflect.Constructor co = cl.getConstructor(classParm);
        		co.newInstance(objectParm);	
        		
            	}
            	
            	for (String key: rusuperobject.biblemap.keySet()) {
            		
            		Document document = new Document();
            		//String indexbiblename = rubibleproperties.rubiblenames[counterbible];
        			String bible = rusuperobject.biblemap.get(key).bible+"";
        			String chapter = rusuperobject.biblemap.get(key).chapter+"";
        			String poem = rusuperobject.biblemap.get(key).poem+"";
        			String poemtext = rusuperobject.biblemap.get(key).poemtext;

        			//document.add(new Field(biblenamefield, indexbiblename, Field.Store.YES, Field.Index.ANALYZED));
        			document.add(new Field(biblefield, bible, Field.Store.YES, Field.Index.ANALYZED));
        			document.add(new Field(chapterfield, chapter, Field.Store.YES, Field.Index.ANALYZED));
        			document.add(new Field(poemfield, poem, Field.Store.YES, Field.Index.ANALYZED));
        			document.add(new Field(poemtextfield, poemtext, Field.Store.YES, Field.Index.ANALYZED));
     			
        			writer.addDocument(document);
        			
        		}
         	
	            
            	
            	
            	writer.close();
	        
	  }
	*/
	  
	  private void addlist(int colglav) {
		  glav.removeAllItems();
		  for (int i = 1; i<=colglav; i++) {
			    String itemglav = "Глава       " + i;
			    glav.addItem(itemglav);
			    }
	  };
	  

    
    public static void main(String[] args) throws IOException, ParseException {
   
    	
    	  SwingUtilities.invokeLater(new Runnable() { 
    	      public void run() { 
    	        try {
					new Createbibleindex();
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LockObtainFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	        
    	        } 
    	    }); 

    }

	@Override
	public void actionPerformed(ActionEvent eva) {
		// TODO Auto-generated method stub
		

		if (eva.getActionCommand().equals("actionsearch")) { 
			if (this.searchbutton.hasFocus()) {
				try {
					searchvoid();
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LockObtainFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (eva.getActionCommand().equals("glavi")) {
			if (this.glav.hasFocus()) {
			int intbook = jcbb.getSelectedIndex()+1;
			int intchapter = glav.getSelectedIndex()+1;
			try {
				biblesettext((jcbb.getSelectedIndex()+1),(glav.getSelectedIndex()+1),"1");
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		else if (eva.getActionCommand().equals("biblebook")) {
			if (this.jcbb.hasFocus()) {
			int intbook = jcbb.getSelectedIndex()+1;
			//int intchapter = glav.getSelectedIndex()+1;
			try {
				biblesettext(intbook,1,"1");
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	}


	private void biblesettext(int book, int chapter, String poem) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// TODO Auto-generated method stub
		
		// вызываем класс с генерированным именем
		String name = "ru.bible.rubible" + book;
		Class cl = Class.forName(name);
		java.lang.reflect.Constructor co = cl.getConstructor(classParm);
		co.newInstance(objectParm);	
        
		String getchapterparameter = "b"+book+"_"+chapter+"_1";
		String str = "";

		//достаем количество глав в книге
		int maxchapter = (Integer) rubibleproperties.rubiblechapters.get("rubible"+book);
		//достаем количество стихов в главе
		int maxpoem = (Integer) rubibleproperties.ruchapterpoems.get("rubible"+book+"_chapter"+chapter);
		//загружаем combobox главами
		
		addlist(maxchapter);
		
		
		for(int intpoem = 1; intpoem <=maxpoem; intpoem++) {
			String getpoemparameter = "b" + book + "_" + chapter + "_" + intpoem;
			str += "<a name='" + rusuperobject.biblemap.get(getpoemparameter).poem + "'></a><font color=gray>" + rusuperobject.biblemap.get(getpoemparameter).poem + "</font> ";
			Integer intpoemfromargument = Integer.parseInt(poem);
			if (intpoem == intpoemfromargument) {
				str += "<b>" + rusuperobject.biblemap.get(getpoemparameter).poemtext + "</b><br>";
			} else {
				str += rusuperobject.biblemap.get(getpoemparameter).poemtext + "<br>";
			}
		}
		
        jta.setText(str);
        
        if(Integer.parseInt(poem) < 2) {
        	jta.setCaretPosition(0);
        }
        jta.scrollToReference(poem);
                
        jcbb.setSelectedIndex(book-1);
        glav.setSelectedIndex(chapter-1);
       
	}

	private void searchvoid() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException {

		
		String querytext = searchfield.getText();

    	Query q = new QueryParser(Version.LUCENE_35, "poemtext", analyzer).parse(querytext);
    	
    	int hitsPerPage = Integer.parseInt((String)searchhits.getSelectedItem());
    	
    	IndexReader reader = IndexReader.open(index);
    	
    	IndexSearcher searcher = new IndexSearcher(reader);
   
    	Sort sort = new Sort(); 
    	sort.setSort(new SortField("bible", SortField.INT));
    	
    	//TopScoreDocCollector collector = TopScoreDocCollector.create( hitsPerPage, true);
    	//TopDocsCollector collector = TopScoreDocCollector.create(hitsPerPage, false);
    	TopFieldCollector collector = TopFieldCollector.create(sort, hitsPerPage, false, true, true, false);
    	
    	//searcher.search(q, collector);
    
    	
    	searcher.search(q, collector);
    	//searcher.search(q, 50, sort);
    	
    	ScoreDoc[] hits = collector.topDocs().scoreDocs;
    	
	    String result = "<b>Результаты поиска: " + hits.length + " совпадений</b><br><br>";
    	
    	for(int i=0;i<hits.length;++i) {
    	    int docId = hits[i].doc;
    	    Document d = searcher.doc(docId);
    	    
    	    String getbible = d.get("bible");
    	    
    	    String namebible = d.get("indexbiblename");
    	    
    	    String getchapter = d.get("chapter");
    	    String getpoem = d.get("poem");
    	    String getpoemtext = d.get("poemtext");

    	    result += "<font color='gray'>Книга " + getbible + ", глава " + getchapter + ", стих " + getpoem + "</font><br>";
    	    result += "<a style='text-decoration:none;color:black;' href=http://null_" + getbible + "_" + getchapter + "_" + getpoem + ">" + getpoemtext + "</a><br><br>";

    	}
    	
    	jta.setText(result);
    	jta.setCaretPosition(0);

	}

    
 
}