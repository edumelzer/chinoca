package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Cliente;
import br.feevale.applogistica.database.orm.ClienteDao;

public class ClienteTest extends AbstractDaoTestLongPk<ClienteDao, Cliente> {

    public ClienteTest() {
        super(ClienteDao.class);
    }

    @Override
    protected Cliente createEntity(Long key) {
        Cliente entity = new Cliente();
        entity.setId(key);
        return entity;
    }

}
