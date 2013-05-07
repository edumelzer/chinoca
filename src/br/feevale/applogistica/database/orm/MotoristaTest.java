package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Motorista;
import br.feevale.applogistica.database.orm.MotoristaDao;

public class MotoristaTest extends AbstractDaoTestLongPk<MotoristaDao, Motorista> {

    public MotoristaTest() {
        super(MotoristaDao.class);
    }

    @Override
    protected Motorista createEntity(Long key) {
        Motorista entity = new Motorista();
        entity.setId(key);
        return entity;
    }

}
