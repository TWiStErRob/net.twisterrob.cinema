package net.twisterrob.test

import java.net.JarURLConnection

/**
 * Reads an entry from `META-INF/MANIFEST.MF`.
 *
 * @param aClassInJar the default value should be copied to all callsites,
 * resulting in the value being read from the right JAR.
 */
internal fun readManifestEntry(name: String, aClassInJar: Class<*> = object {}.javaClass.enclosingClass): String {
	val aClassName = aClassInJar.simpleName + ".class"
	val res = aClassInJar.getResource(aClassName) ?: error("Cannot find class file ${aClassName}")
	val url = res.openConnection() ?: error("Cannot open ${res}")
	url as? JarURLConnection ?: error("Unsupported packaging mechanism: ${url}, no JAR file to get manifest from.")
	val mf = url.manifest ?: error("Cannot find manifest in ${url.jarFileURL}")
	val version = mf.mainAttributes.getValue(name)
		?: error("${name} attribute not present in manifest\n${url.manifest.mainAttributes.toMap()}")
	return version
}
