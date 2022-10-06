package net.twisterrob.cinema.cineworld.quickbook

import dagger.BindsInstance
import dagger.Module
import javax.inject.Named

/**
 * @see QuickbookModule.Dependencies when using this module.
 */
@Module
object QuickbookModule {

	/**
	 * Inherit this from [dagger.Component.Builder] for the component to get a consistent dependency setup.
	 *
	 * @param Builder actual type of the [dagger.Component.Builder]
	 */
	interface Dependencies<Builder> {

		/**
		 * For most cases just call this method without any argument, the default will work.
		 */
		@BindsInstance
		fun quickbookApiKey(@Named(API_KEY) key: String = "9qfgpF7B"): Builder
	}

	internal const val API_KEY = "quickbook_api_key"
}
