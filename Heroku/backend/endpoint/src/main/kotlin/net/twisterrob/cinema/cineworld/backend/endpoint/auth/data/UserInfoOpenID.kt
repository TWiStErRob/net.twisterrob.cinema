package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import java.net.URI

/**
 * On the consent screen this message is shown:
 * > To continue, Google will share your name, email address, language preference and profile picture with TWiStErRob's Cinema Tools.
 *
 * @see https://developers.google.com/identity/protocols/OpenIDConnect#an-id-tokens-payload
 * @since defines the scope which is required during authorization to get this field
 */
data class UserInfoOpenID(
	/**
	 * An identifier for the user, unique among all Google accounts and never reused.
	 * A Google account can have multiple email addresses at different points in time, but the sub value is never changed.
	 * Use sub within your application as the unique-identifier key for the user.
	 * Maximum length of 255 case-sensitive ASCII characters.
	 * @since always there
	 */
	val sub: String,

	/**
	 * The user's email address.
	 * This value may not be unique to this user and is not suitable for use as a primary key.
	 * _Provided only if your scope included the email scope value._
	 * @exception NullPointerException if email scope was missing during auth
	 * @since email
	 */
	val email: String?,

	/**
	 * True if the user's e-mail address has been verified; otherwise false.
	 * @since email
	 */
	val email_verified: Boolean?,

	/**
	 * The URL of the user's profile picture. Might be provided when:
	 *  * The request scope included the string "profile"
	 *  * The ID token is returned from a token refresh
	 * When picture claims are present, you can use them to update your app's user records.
	 * _Note that this claim is never guaranteed to be present._
	 * @since email, profile
	 */
	val picture: URI?,

	/**
	 * The user's full name, in a displayable form. Might be provided when:
	 *  * The request scope included the string "profile"
	 *  * The ID token is returned from a token refresh
	 * When [name] claims are present, you can use them to update your app's user records.
	 * _Note that this claim is never guaranteed to be present._
	 * @since profile
	 */
	val name: String?,

	/**
	 * The user's given name(s) or first name(s).
	 * _Might be provided when a [name] claim is present._
	 * @since profile
	 */
	val given_name: String?,

	/**
	 * The user's surname(s) or last name(s).
	 * _Might be provided when a [name] claim is present._
	 * @since profile
	 */
	val family_name: String?,

	/**
	 * The user's locale, represented by a [BCP 47](https://tools.ietf.org/html/bcp47) language tag.
	 * _Might be provided when a [name] claim is present._
	 * @since profile
	 */
	val locale: String?
) {


	companion object {
		/**
		 * @see userinfo_endpoint https://accounts.google.com/.well-known/openid-configuration
		 * TODO was https://www.googleapis.com/userinfo/v2/me in Ktor docs
		 */
		val URL: URI = URI.create("https://openidconnect.googleapis.com/v1/userinfo")
	}

	object Scopes {

		/**
		 * Scope for [name] ([given_name], [family_name]), [picture], [locale].
		 */
		const val profile = "profile"

		/**
		 * Scope for [email] and [email_verified] claims.
		 * Also sends [picture] for some reason.
		 */
		const val email = "email"

		/**
		 * Scope for [sub] claim.
		 * Also sends [picture] for some reason.
		 */
		const val openid = "openid"
	}
}
