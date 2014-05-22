package it.poli.android.scoutthisme.tools;

import android.graphics.Bitmap;

public class NewsItem {
    private String title;
    private String url;
    private String description;
    private Bitmap image;
 
    public NewsItem() {
        super();
        this.setTitle("");
        this.setUrl("");
        this.setDescription("");
        this.setImage(null);
    }
    
    public NewsItem(String title, String url, String description, Bitmap image) {
        super();
        this.setTitle(title);
        this.setUrl(url);
        this.setDescription(description);
        this.setImage(image);
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
}
