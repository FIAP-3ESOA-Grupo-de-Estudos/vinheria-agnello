package br.com.fiap.pbl3.dao;

import java.util.List;

public interface DAO<T, ID> {
    void inserir(T entidade) throws Exception;
    T buscarPorId(ID id) throws Exception;
    List<T> listarTodos() throws Exception;
    void atualizar(T entidade) throws Exception;
    void deletar(ID id) throws Exception;
}
