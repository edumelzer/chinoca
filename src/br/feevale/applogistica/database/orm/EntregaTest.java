package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Entrega;
import br.feevale.applogistica.database.orm.EntregaDao;

public class EntregaTest extends AbstractDaoTestLongPk<EntregaDao, Entrega> {

    public EntregaTest() {
        super(EntregaDao.class);
    }

    @Override
    protected Entrega createEntity(Long key) {
        Entrega entity = new Entrega();
        entity.setId(key);
        return entity;
    }

}
