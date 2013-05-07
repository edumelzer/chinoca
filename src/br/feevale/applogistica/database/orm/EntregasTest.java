package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Entregas;
import br.feevale.applogistica.database.orm.EntregasDao;

public class EntregasTest extends AbstractDaoTestLongPk<EntregasDao, Entregas> {

    public EntregasTest() {
        super(EntregasDao.class);
    }

    @Override
    protected Entregas createEntity(Long key) {
        Entregas entity = new Entregas();
        entity.setId(key);
        return entity;
    }

}
