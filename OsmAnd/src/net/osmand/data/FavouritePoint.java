package net.osmand.data;

import java.io.Serializable;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import net.osmand.GPXUtilities.WptPt;
import net.osmand.plus.R;

public class FavouritePoint implements Serializable, LocationPoint {
	private static final long serialVersionUID = 729654300829771466L;

	private static final String HIDDEN = "hidden";
	public static final String PERSONAL_CATEGORY = "personal";

	protected String name = "";
	protected String description;
	protected String category = "";
	private String originObjectName = "";
	private double latitude;
	private double longitude;
	private int color;
	private boolean visible = true;

	public enum PointType {
		HOME("home", R.string.home_button, 1, R.drawable.ic_action_home_dark),
		WORK("work", R.string.work_button, 2, R.drawable.ic_action_work),
		PARKING("parking", R.string.map_widget_parking, 3, R.drawable.ic_action_parking_dark);

		private String typeName;
		@StringRes
		private int resId;
		private int order;
		@DrawableRes
		private int iconId;

		PointType(@NonNull String typeName, @StringRes int resId, int order, @DrawableRes int iconId) {
			this.typeName = typeName;
			this.resId = resId;
			this.order = order;
			this.iconId = iconId;
		}

		public String getName() {
			return typeName;
		}

		public int getOrder() {
			return order;
		}

		public static PointType valueOfTypeName(@NonNull String typeName) {
			for (PointType pt : values()) {
				if (pt.typeName.equals(typeName)) {
					return pt;
				}
			}
			throw new IllegalArgumentException("Illegal PointType typeName");
		}

		public int getIconId() {
			return iconId;
		}

		public String getHumanString(@NonNull Context ctx) {
			return ctx.getString(resId);
		}
	}

	public FavouritePoint(){
	}

	public FavouritePoint(double latitude, double longitude, String name, String category) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.category = category;
		if(name == null) {
			name = "";
		}
		this.name = name;
	}

	public FavouritePoint(FavouritePoint favouritePoint) {
		this.latitude = favouritePoint.latitude;
		this.longitude = favouritePoint.longitude;
		this.category = favouritePoint.category;
		this.name = favouritePoint.name;
		this.color = favouritePoint.color;
		this.description = favouritePoint.description;
		this.visible = favouritePoint.visible;
		this.originObjectName = favouritePoint.originObjectName;
	}

	public int getColor() {
		return color;
	}
	
	public PointDescription getPointDescription() {
		return new PointDescription(PointDescription.POINT_TYPE_FAVORITE, getName());
	}

	public boolean isPersonal() {
		return PERSONAL_CATEGORY.equals(category);
	}

	@Override
	public PointDescription getPointDescription(@NonNull Context ctx) {
		if (isPersonal()) {
			return new PointDescription(PointDescription.POINT_TYPE_FAVORITE, getName(ctx));
		} else {
			return getPointDescription();
		}
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getOriginObjectName() {
		return originObjectName;
	}

	public void setOriginObjectName(String originObjectName) {
		this.originObjectName = originObjectName;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getCategory() {
		return category;
	}

	public String getCategory(@NonNull Context ctx) {
		if (isPersonal()) {
			return ctx.getString(R.string.personal_category_name);
		} else {
			return category;
		}
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public String getName(@NonNull Context ctx) {
		if (isPersonal()) {
			return PointType.valueOfTypeName(getName()).getHumanString(ctx);
		}
		return name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NonNull
	@Override
	public String toString() {
		return "Favourite " + getName(); //$NON-NLS-1$
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null) return false;

		if (getClass() != o.getClass()) return false;

		FavouritePoint fp = (FavouritePoint) o;

		if (name == null) {
			if (fp.name != null)
				return false;
		} else if (!name.equals(fp.name))
			return false;

		if (category == null) {
			if (fp.category != null)
				return false;
		} else if (!category.equals(fp.category))
			return false;

		if (description == null) {
			if (fp.description != null)
				return false;
		} else if (!description.equals(fp.description))
			return false;

		if (originObjectName == null) {
			if (fp.originObjectName != null)
				return false;
		} else if (!originObjectName.equals(fp.originObjectName))
			return false;

		return (this.latitude == fp.latitude) && (this.longitude == fp.longitude);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) Math.floor(latitude * 10000);
		result = prime * result + (int) Math.floor(longitude * 10000);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((originObjectName == null) ? 0 : originObjectName.hashCode());
		return result;
	}

	public static FavouritePoint fromWpt(@NonNull WptPt pt) {
		String name = pt.name;
		String categoryName = pt.category != null ? pt.category : "";
		if (name == null) {
			name = "";
		}
		FavouritePoint fp;
			fp = new FavouritePoint(pt.lat, pt.lon, name, categoryName);
		fp.setDescription(pt.desc);
		if (pt.comment != null) {
			fp.setOriginObjectName(pt.comment);
		}
		fp.setColor(pt.getColor(0));
		fp.setVisible(!pt.getExtensionsToRead().containsKey(HIDDEN));
		return fp;
	}

	public WptPt toWpt() {
		WptPt pt = new WptPt();
		pt.lat = getLatitude();
		pt.lon = getLongitude();
		if (!isVisible()) {
			pt.getExtensionsToWrite().put(HIDDEN, "true");
		}
		if (getColor() != 0) {
			pt.setColor(getColor());
		}
		pt.name = getName();
		pt.desc = getDescription();
		if (getCategory().length() > 0)
			pt.category = getCategory();
		if (getOriginObjectName().length() > 0) {
			pt.comment = getOriginObjectName();
		}
		return pt;
	}
}