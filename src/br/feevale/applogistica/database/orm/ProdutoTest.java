package br.feevale.applogistica.database.orm;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import br.feevale.applogistica.database.orm.Produto;
import br.feevale.applogistica.database.orm.ProdutoDao;

public class ProdutoTest extends AbstractDaoTestLongPk<ProdutoDao, Produto> {

    public ProdutoTest() {
        super(ProdutoDao.class);
    }

    @Override
    protected Produto createEntity(Long key) {
        Produto entity = new Produto();
        entity.setId(key);
        return entity;
    }

}
