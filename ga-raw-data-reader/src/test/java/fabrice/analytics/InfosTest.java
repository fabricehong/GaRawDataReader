package fabrice.analytics;

import com.google.common.collect.ImmutableMap;
import fabrice.domain.Infos;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 14.08.15
 */
public class InfosTest {

	@Test
	public void testEquals() {
		Infos map1 = new Infos(ImmutableMap.of(
				"sdf", "sdf"
		));
		Infos map2 = new Infos(ImmutableMap.of(
				"sdf", "sdf"
		));
		Assert.assertEquals(map1, map2);
	}

	@Test
	public void testEqualsMoreValueInInitial() {
		Infos map1 = new Infos(ImmutableMap.of(
				"sdf", "sdf",
				"sdf2", "sdf2"
		));
		Infos map2 = new Infos(ImmutableMap.of(
				"sdf", "sdf"
		));
		Assert.assertNotEquals(map1, map2);
	}

	@Test
	public void testEqualsMoreValueInSecond() {
		Infos map1 = new Infos(ImmutableMap.of(
				"sdf", "sdf"
		));
		Infos map2 = new Infos(ImmutableMap.of(
				"sdf", "sdf",
				"sdf2", "sdf2"
		));
		Assert.assertNotEquals(map1, map2);
	}

}