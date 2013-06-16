package org.objectquery.hibernateobjectquery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectquery.generic.ConditionElement;
import org.objectquery.generic.ConditionGroup;
import org.objectquery.generic.ConditionItem;
import org.objectquery.generic.ConditionType;
import org.objectquery.generic.GenericInternalQueryBuilder;
import org.objectquery.generic.GenericObjectQuery;
import org.objectquery.generic.Having;
import org.objectquery.generic.Join;
import org.objectquery.generic.JoinType;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.Order;
import org.objectquery.generic.PathItem;
import org.objectquery.generic.Projection;
import org.objectquery.generic.ProjectionType;

public class HQLQueryGenerator {

	private Map<String, Object> parameters = new LinkedHashMap<String, Object>();
	private String query;

	HQLQueryGenerator(GenericObjectQuery<?> hqlObjectQuery) {
		if (hqlObjectQuery.getRootPathItem().getName() == null || hqlObjectQuery.getRootPathItem().getName().isEmpty()) {
			hqlObjectQuery.getRootPathItem().setName("A");
		}
		buildQuery(hqlObjectQuery.getTargetClass(), (GenericInternalQueryBuilder) hqlObjectQuery.getBuilder(), hqlObjectQuery.getJoins(), hqlObjectQuery
				.getRootPathItem().getName());
	}

	private void stringfyGroup(ConditionGroup group, StringBuilder builder) {
		if (!group.getConditions().isEmpty()) {
			Iterator<ConditionElement> eli = group.getConditions().iterator();
			while (eli.hasNext()) {
				ConditionElement el = eli.next();
				if (el instanceof ConditionItem) {
					stringfyCondition((ConditionItem) el, builder);
				} else if (el instanceof ConditionGroup) {
					builder.append(" ( ");
					stringfyGroup((ConditionGroup) el, builder);
					builder.append(" ) ");
				}
				if (eli.hasNext()) {
					builder.append(" ").append(group.getType().toString()).append(" ");
				}
			}
		}
	}

	private String getConditionType(ConditionType type) {
		switch (type) {
		case CONTAINS:
			return " member of ";
		case EQUALS:
			return " = ";
		case IN:
			return " in ";
		case LIKE:
			return " like ";
		case GREATER:
			return " > ";
		case LESS:
			return " < ";
		case GREATER_EQUALS:
			return " >= ";
		case LESS_EQUALS:
			return " <= ";
		case NOT_CONTAINS:
			return " not member of ";
		case NOT_EQUALS:
			return " <> ";
		case NOT_IN:
			return " not in ";
		case NOT_LIKE:
			return "not like";
		case LIKE_NOCASE:
			return " like ";
		case NOT_LIKE_NOCASE:
			return " not like ";
		case BETWEEN:
			return " BETWEEN ";
		}
		return "";
	}

	private void buildName(PathItem item, StringBuilder sb) {
		GenericInternalQueryBuilder.buildPath(item, sb);
	}

	private String buildParameterName(PathItem item, Object value) {
		StringBuilder name = new StringBuilder();
		buildParameterName(item, name);
		int i = 1;
		String realName = name.toString();
		do {
			if (!parameters.containsKey(realName)) {
				parameters.put(realName, value);
				return realName;
			}
			realName = name.toString() + i++;
		} while (true);
	}

	private void stringfyCondition(ConditionItem cond, StringBuilder sb) {

		if (cond.getType().equals(ConditionType.LIKE_NOCASE) || cond.getType().equals(ConditionType.NOT_LIKE_NOCASE)) {
			sb.append("UPPER(");
			buildName(cond.getItem(), sb);
			sb.append(")");
			sb.append(getConditionType(cond.getType()));
			sb.append("UPPER(");
			conditionValue(cond, sb);
			sb.append(")");
		} else if (cond.getType().equals(ConditionType.CONTAINS) || cond.getType().equals(ConditionType.NOT_CONTAINS)) {
			conditionValue(cond, sb);
			sb.append(" ").append(getConditionType(cond.getType())).append(" ");
			buildName(cond.getItem(), sb);
		} else {
			buildName(cond.getItem(), sb);
			sb.append(" ").append(getConditionType(cond.getType())).append(" ");
			if (cond.getType().equals(ConditionType.IN) || cond.getType().equals(ConditionType.NOT_IN))
				sb.append("(");
			conditionValue(cond, sb);
			if (cond.getType().equals(ConditionType.IN) || cond.getType().equals(ConditionType.NOT_IN))
				sb.append(")");
			if (cond.getType().equals(ConditionType.BETWEEN)) {
				sb.append(" AND ");
				conditionValueTo(cond, sb);
				sb.append(" ");
			}
		}
	}

	private void conditionValue(ConditionItem cond, StringBuilder sb) {
		if (cond.getValue() instanceof PathItem) {
			buildName((PathItem) cond.getValue(), sb);
		} else if (cond.getValue() instanceof GenericObjectQuery<?>) {
			buildSubquery(sb, (GenericObjectQuery<?>) cond.getValue());
		} else {
			sb.append(":");
			sb.append(buildParameterName(cond.getItem(), cond.getValue()));
		}
	}

	private void conditionValueTo(ConditionItem cond, StringBuilder sb) {
		if (cond.getValueTo() instanceof PathItem) {
			buildName((PathItem) cond.getValue(), sb);
		} else {
			sb.append(":");
			sb.append(buildParameterName(cond.getItem(), cond.getValueTo()));
		}
	}

	private String resolveFunction(ProjectionType projectionType) {
		switch (projectionType) {
		case AVG:
			return "AVG";
		case MAX:
			return "MAX";
		case MIN:
			return "MIN";
		case COUNT:
			return "COUNT";
		case SUM:
			return "SUM";
		}
		return "";
	}

	public void buildQuery(Class<?> clazz, GenericInternalQueryBuilder query, List<Join> joins, String prefix) {
		parameters.clear();
		StringBuilder builder = new StringBuilder();
		buildQueryString(clazz, query, joins, builder, prefix);
		this.query = builder.toString();
	}

	public void buildQueryString(Class<?> clazz, GenericInternalQueryBuilder query, List<Join> joins, StringBuilder builder, String prefix) {
		List<Projection> groupby = new ArrayList<Projection>();
		boolean grouped = false;
		builder.append("select ");
		if (!query.getProjections().isEmpty()) {
			Iterator<Projection> projections = query.getProjections().iterator();
			while (projections.hasNext()) {
				Projection proj = projections.next();
				if (proj.getType() != null) {
					builder.append(" ").append(resolveFunction(proj.getType())).append("(");
					grouped = true;
				} else
					groupby.add(proj);
				if (proj.getItem() instanceof PathItem)
					buildName((PathItem) proj.getItem(), builder);
				else
					buildSubquery(builder, (GenericObjectQuery<?>) proj.getItem());
				if (proj.getType() != null)
					builder.append(")");
				if (projections.hasNext())
					builder.append(",");
			}
		} else
			builder.append(prefix);
		builder.append(" from ").append(clazz.getName()).append(" ").append(prefix);

		for (Join join : joins) {
			if (join.getJoinPath() != null) {
				builder.append(getJoinType(join.getType()));
				buildName(join.getJoinPath(), builder);
			} else {
				if (join.getType() != JoinType.INNER)
					throw new ObjectQueryException("not suppurted join type:" + join.getType() + " without specify a join path");
				builder.append(",").append(join.getJavaType().getName());
			}
			builder.append(" ").append(join.getRoot().getName());
		}

		if (!query.getConditions().isEmpty()) {
			builder.append(" where ");
			stringfyGroup(query, builder);
		}

		boolean orderGrouped = false;
		for (Order ord : query.getOrders()) {
			if (ord.getProjectionType() != null) {
				orderGrouped = true;
				break;
			}
		}

		if ((orderGrouped || grouped) && !groupby.isEmpty()) {
			builder.append(" group by ");
			Iterator<Projection> projections = groupby.iterator();
			while (projections.hasNext()) {
				Projection proj = projections.next();
				if (proj.getItem() instanceof PathItem)
					buildName((PathItem) proj.getItem(), builder);
				else
					buildSubquery(builder, (GenericObjectQuery<?>) proj.getItem());
				if (projections.hasNext())
					builder.append(",");
			}
		} else if (orderGrouped && query.getProjections().isEmpty()) {
			builder.append(" group by ").append(prefix).append(" ");
		}

		if (!query.getHavings().isEmpty()) {
			builder.append(" having");
			Iterator<Having> havings = query.getHavings().iterator();
			while (havings.hasNext()) {
				Having having = havings.next();
				builder.append(" ").append(resolveFunction(having.getProjectionType())).append('(');
				if (having.getItem() instanceof PathItem)
					buildName((PathItem) having.getItem(), builder);
				else
					throw new ObjectQueryException("unsupported subquery in the having clause for hql", null);
				builder.append(')').append(getConditionType(having.getConditionType()));
				builder.append(":");
				builder.append(buildParameterName((PathItem) having.getItem(), having.getValue()));
				if (havings.hasNext())
					builder.append(" AND");

			}
		}

		if (!query.getOrders().isEmpty()) {
			builder.append(" order by ");
			Iterator<Order> orders = query.getOrders().iterator();
			while (orders.hasNext()) {
				Order ord = orders.next();
				if (ord.getProjectionType() != null)
					builder.append(" ").append(resolveFunction(ord.getProjectionType())).append("(");
				if (ord.getItem() instanceof PathItem)
					buildName((PathItem) ord.getItem(), builder);
				else {
					throw new ObjectQueryException("Operation not supported by hql", null);
				}
				if (ord.getProjectionType() != null)
					builder.append(")");
				if (ord.getType() != null)
					builder.append(" ").append(ord.getType());
				if (orders.hasNext())
					builder.append(',');
			}
		}
		this.query = builder.toString();
	}

	private void buildSubquery(StringBuilder builder, GenericObjectQuery<?> goq) {
		builder.append("(");
		buildQueryString(goq.getTargetClass(), (GenericInternalQueryBuilder) goq.getBuilder(), goq.getJoins(), builder, goq.getRootPathItem().getName());
		builder.append(")");
	}

	private String getJoinType(JoinType type) {
		switch (type) {
		case INNER:
			return " INNER JOIN ";
		case LEFT:
			return " LEFT JOIN ";
		case RIGHT:
			return " RIGHT JOIN ";
		case OUTER:
			return " OUTER JOIN ";
		}
		return "";

	}

	private void buildParameterName(PathItem conditionItem, StringBuilder builder) {
		GenericInternalQueryBuilder.buildPath(conditionItem, builder, "_");
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getQuery() {
		return query;
	}
}
