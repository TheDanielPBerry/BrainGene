package game;

/**
 * Models are shallow representations of what will become articles. This allows for minimum memory hogging and allows several
 * instances of the same article to share things like name and bounding boxes.
 * @author Daniel Berry
 */
public class Model {
	
	
	public String name;
	public String img;
	public Box src;
	public Box[] boxes;
	public Class<Article> myClass;
}
