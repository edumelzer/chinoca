package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Produtos;
import br.feevale.applogistica.database.orm.ProdutosDao;

public class ProdutosTest extends AbstractDaoTestLongPk<ProdutosDao, Produtos> {

    public ProdutosTest() {
        super(ProdutosDao.class);
    }

    @Override
    protected Produtos createEntity(Long key) {
        Produtos entity = new Produtos();
        entity.setId(key);
        return entity;
    }

}
