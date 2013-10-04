package com.nextbigsound.tunebot.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nextbigsound.tunebot.utils.SongResolver;

public class SongResolverTest {

	@Test
	public void test() {

			assertEquals("spotify:user:livbuli:playlist:2INcQWFql0Tf62aOvpCr4G", SongResolver.resolveSong("spotify:user:livbuli:playlist:2INcQWFql0Tf62aOvpCr4G"));
			assertEquals("spotify:local:Walter+Blaurock:Walter+Blaurock%27s+Album:sevenfour:54", SongResolver.resolveSong("spotify:local:Walter+Blaurock:Walter+Blaurock%27s+Album:sevenfour:54"));
			
			assertNull(SongResolver.resolveSong("spotify:user::playlist:2INcQWFql0Tf62aOvpCr4G"));
			assertNull(SongResolver.resolveSong("spotify:local:"));
	
	}

}
