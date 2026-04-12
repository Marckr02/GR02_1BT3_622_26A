package dao;

import java.util.List;
import model.ItemMenu;

public interface ItemMenuDao extends GenericDao<ItemMenu, Long> {
	List<ItemMenu> findAllWithInsumos();
}

