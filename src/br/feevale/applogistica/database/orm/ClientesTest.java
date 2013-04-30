package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Clientes;
import br.feevale.applogistica.database.orm.ClientesDao;

public class ClientesTest extends AbstractDaoTestLongPk<ClientesDao, Clientes> {

    public ClientesTest() {
        super(ClientesDao.class);
    }

    @Override
    protected Clientes createEntity(Long key) {
        Clientes entity = new Clientes();
        entity.setId(key);
        return entity;
    }

}
