package estore.repository.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import estore.admin.bean.AccountFilter;
import estore.repository.Account;
import estore.repository.AccountDAO;
import estore.repository.Authority;
import estore.repository.AuthorityDAO;
import estore.repository.Role;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	AccountDAO dao;
	
	@Autowired
	AuthorityDAO authorityDao;

	@Override
	public Account getByUsername(String username) {
		return dao.getById(username);
	}

	@Override
	public void deleteByUsername(String username) {
		dao.deleteById(username);
	}

	@Transactional
	@Override
	public void create(Account item, List<String> roleIds) {
		List<Authority> authorities = roleIds.stream()
			.map(rid -> new Authority(item, new Role(rid))).collect(Collectors.toList());
		
		item.setAuthorities(authorities);
		dao.save(item);
		authorityDao.saveAll(authorities);
	}

	@Transactional
	@Override
	public void update(Account item, List<String> roleIds) {
		authorityDao.deleteAll(dao.getById(item.getUsername()).getAuthorities());
		dao.save(item);
		if(!roleIds.isEmpty()) {
			List<Authority> authorities = roleIds.stream()
				.map(rid -> new Authority(item, new Role(rid))).collect(Collectors.toList());
			item.setAuthorities(authorities);
			authorityDao.saveAll(authorities);
		}
	}
	
	@Override
	public Page<Account> findPageByFilter(AccountFilter filter, Pageable pageable) {
		String keyword = "%"+filter.getKeyword()+"%";
		// no role
		if(filter.getRole() == 2) { 
			if(filter.getActivated() == 2) {
				return dao.findAccountByKeyword(keyword, pageable);
			}
			return dao.findAccountByKeywordAndActivated(keyword, filter.getActivated() == 1, pageable);
		}
		// customer
		if(filter.getRole() == 0) { 
			if(filter.getActivated() == 2) {
				return dao.findCustomerByKeyword(keyword, pageable);
			}
			return dao.findCustomerByKeywordAndActivated(keyword, filter.getActivated() == 1, pageable);
		}
		// master
		if(filter.getActivated() == 2) {
			return dao.findMasterByKeyword(keyword, pageable);
		}
		return dao.findMasterByKeywordAndActivated(keyword, filter.getActivated() == 1, pageable);
	}

	@Override
	public boolean existByUsername(String username) {
		return dao.existsById(username);
	}
}