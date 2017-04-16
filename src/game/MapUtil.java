package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MapUtil {
	
	
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Model> LoadModels(String dbFilePath) {
		HashMap<String, Model> models = new HashMap<String, Model>();
		BufferedReader reader;
		try {
			FileReader fReader = new FileReader(dbFilePath);
			reader = new BufferedReader(fReader);
			String line = reader.readLine();
			String tags[] = line.split(",", -1);
			while((line = reader.readLine()) != null) {
				HashMap<String,String> valueMap = AssignColumns(tags, line);
				Model m = new Model();
				m.name = valueMap.get("Primary Id");
				m.img = valueMap.get("Source Image");
				m.src = new Box(Integer.parseInt(valueMap.get("sx")),
						Integer.parseInt(valueMap.get("sy")),
						Integer.parseInt(valueMap.get("sw")),
						Integer.parseInt(valueMap.get("sh")));
				if(Boolean.parseBoolean(valueMap.get("Class"))) {
					try {
						m.myClass = (Class<Article>) Class.forName("game.articles." + m.name.substring(0,1).toUpperCase()+m.name.substring(1));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				models.put(m.name, m);
			}
			reader.close();
			fReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return models;
	}
	
	public static HashMap<String, String> AssignColumns(String[] headers, String line) {
		HashMap<String, String> tags = new HashMap<String, String>();
		String values[] = line.split(",", -1);
		for(short i=0; i<values.length; i++) {
			tags.put(headers[i], values[i]);
		}
		return tags;
	}
	
	
	
	public static HashMap<String, ArrayList<Box>> GroupBoxes(String fileName) {
		HashMap<String, ArrayList<Box>> boxes = new HashMap<String, ArrayList<Box>>();
		BufferedReader reader;
		try {
			FileReader fReader = new FileReader(fileName);
			reader = new BufferedReader(fReader);
			String line = reader.readLine();
			String tags[] = line.split(",", -1);
			while((line = reader.readLine()) != null) {
				HashMap<String,String> valueMap = AssignColumns(tags, line);
				Box b = new Box(Integer.parseInt(valueMap.get("X")),
								Integer.parseInt(valueMap.get("Y")),
								Integer.parseInt(valueMap.get("Width")),
								Integer.parseInt(valueMap.get("Height")));
				if(boxes.containsValue(valueMap.get("Foreign Id"))) {
					boxes.get(valueMap.get("Foreign Id")).add(b);
				}else {
					ArrayList<Box> list = new ArrayList<Box>();
					list.add(b);
					boxes.put(valueMap.get("Foreign Id"), list);
				}
			}
			reader.close();
			fReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return boxes;
	}
	
	
	public static HashMap<String, Model> AssignBoxes(HashMap<String, Model> models, String fileName) {
		HashMap<String, ArrayList<Box>> alikeBoxes = GroupBoxes(fileName);
		for(Entry<String, ArrayList<Box>> entry : alikeBoxes.entrySet()) {
			models.get(entry.getKey()).boxes = new Box[entry.getValue().size()];
			models.get(entry.getKey()).boxes = entry.getValue().toArray(models.get(entry.getKey()).boxes);	
		}
		return models;
	}
	
	public static ArrayList<Article> SubList(NodeList nodeList, Vector2f offset, HashMap<String, Model> models) {
		ArrayList<Article> articles = new ArrayList<Article>();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				NamedNodeMap attr = node.getAttributes();
				if(!node.hasChildNodes()) {
					int width = -1;
					int height = -1;
					Node wAttr= attr.getNamedItem("w");
					Node hAttr= attr.getNamedItem("h");
					if(wAttr!=null) {
						width = Integer.parseInt(wAttr.getTextContent());
					}
					if(hAttr!=null) {
						height = Integer.parseInt(hAttr.getTextContent());
					}
					Model m = models.get(node.getNodeName());
					Article article = new Article();
					if(m.myClass != null) {
						try {
							Constructor<Article> con = m.myClass.getDeclaredConstructor(Class.forName("game.Model"),Class.forName("game.Box"));
							article = con.newInstance(m, new Box(Integer.parseInt(attr.getNamedItem("x").getTextContent())+(int)(offset.x),
									Integer.parseInt(attr.getNamedItem("y").getTextContent())+(int)(offset.y),
			    					width, height));
						} catch (NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						}catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (DOMException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						article = new Article(m, new Box(Integer.parseInt(attr.getNamedItem("x").getTextContent())+(int)(offset.x),
						Integer.parseInt(attr.getNamedItem("y").getTextContent())+(int)(offset.y),
    					width, height));
					}
					Node cam = attr.getNamedItem("camera");
					if(cam!=null && cam.getTextContent().toLowerCase().equals("true")) {
						Roger.cameraId = articles.size();
					}
					Node staticAttr;
					if((staticAttr = attr.getNamedItem("static")) != null) {
						article.staticElement = Boolean.parseBoolean(staticAttr.getTextContent());
					}
					articles.add(article);
				} else if(node.getNodeName().toLowerCase().equals("strip")) {
					Node xAttr = attr.getNamedItem("xstep");
					Node yAttr = attr.getNamedItem("ystep");
					Node countAttr = attr.getNamedItem("count");
					float x = 0f;
					float y = 0f;
					short count = 0;
					if(xAttr!=null) {
						x=Float.parseFloat(xAttr.getTextContent());
					}
					if(yAttr!=null) {
						y=Float.parseFloat(yAttr.getTextContent());
					}
					if(countAttr!=null) {
						count=Short.parseShort(countAttr.getTextContent());
					}
					for(short k=0; k<count; k++) {
						articles.addAll(SubList(node.getChildNodes(), new Vector2f(offset.x+(k*x),offset.y+(k*y)), models));
					}
				}
			}
		}
		
		return articles;
	}
	
	
	public static void LoadMap(ArrayList<Article> articles, String mapPath) {
		try {
			File inputFile = new File(mapPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			Element world = doc.getDocumentElement();
			world.normalize();
			HashMap<String, Model> models = MapUtil.LoadModels(Roger.relativeFilePath + "assets/models/" + world.getAttribute("src") + "_models.csv");
			models = MapUtil.AssignBoxes(models, Roger.relativeFilePath + "assets/models/" + world.getAttribute("src") + "_boxes.csv");
			NodeList nodeList = world.getChildNodes();
			articles.addAll(SubList(nodeList, new Vector2f(0,0), models));
			Roger.models = models;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	public static void LoadMap(ArrayList<Article> articles) {
		
	}
}
