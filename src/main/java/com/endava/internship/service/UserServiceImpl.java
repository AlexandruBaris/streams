package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted(Comparator.comparing(User::getAge).reversed().thenComparing(User::getFirstName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return  users.stream()
                .filter(user -> user.getAge() > age && user.getPrivileges().contains(Privilege.UPDATE))
                .findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(k -> k.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users.stream()
                .mapToInt(User::getAge)
                .average()
                .orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::getLastName, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .reduce((e1, e2) -> e1.getValue() < e2.getValue()? e2:
                        e1.getValue() > e2.getValue()? e1:
                                new AbstractMap.SimpleImmutableEntry<>(null, e1.getValue()))
                .map(Map.Entry::getKey);
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Arrays.stream(predicates).reduce(predicate -> true, Predicate::and))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(mapFun)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream()
                        .map(privilege -> Pair.of(privilege, user)))
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));

    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(User::getLastName , Collectors.counting()));
    }
}
