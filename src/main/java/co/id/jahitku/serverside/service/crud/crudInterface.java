/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service.crud;

import java.util.List;

/**
 *
 * @author Firli
 */
public interface crudInterface<T> {
    public List<T> getAll();
    public T getById(Long id);
    public T delete(Long id);
}
