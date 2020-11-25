package com.fredericoahb.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fredericoahb.dscatalog.dto.CategoryDTO;
import com.fredericoahb.dscatalog.entities.Category;
import com.fredericoahb.dscatalog.repositories.CategoryRepository;
import com.fredericoahb.dscatalog.services.exceptions.DatabaseException;
import com.fredericoahb.dscatalog.services.exceptions.EntityNotFoundException;
import com.fredericoahb.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();

		// to transform a List<Category> to List<CategoryDTO>
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id); // findById returns an Optional.
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not Found")); // to get as
																									// Category the
																									// Optional object
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		try {
			Category entity = repository.getOne(id); // .getOne() dont touch the DB. Instantiates a provisory object
														// with that ID. Just touch the DB when to save
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found:" + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found.");
		} catch (DataIntegrityViolationException e) { // integrity. in case that there are products with the category, cant be deleted
			throw new DatabaseException("Integrity violaton.");
		}
	}

}
