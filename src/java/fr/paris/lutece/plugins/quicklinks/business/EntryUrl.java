/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.quicklinks.business;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.quicklinks.service.EntryUrlService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * The class Entry Text
 * 
 */
public class EntryUrl extends Entry
{
    private static final String EMPTY_STRING = "";
    private static final String REGEX_ID = "^[\\d]+$";
    private static final int DISPLAY_PROPERTIES_DEFAULT_VALUE = 1;
    private static final int LINK_PROPERTIES_DEFAULT_VALUE = 1;
    private static final String TARGET_DEFAULT_VALUE = "_blank";

    // Templates
    private static final String TEMPLATE_DISPLAY = "skin/plugins/quicklinks/entry_url.html";

    // Markers
    private static final String MARK_TARGET_DEFAULT_VALUE = "target_default_value";
    private static final String MARK_DISPLAY_PROPERTIES_LIST = "display_properties_list";
    private static final String MARK_DISPLAY_PROPERTIES_DEFAULT_VALUE = "display_properties_default_value";
    private static final String MARK_LINK_PROPERTIES_LIST = "link_properties_list";
    private static final String MARK_LINK_PROPERTIES_DEFAULT_VALUE = "link_properties_default_value";
    private static final String MARK_ENTRY_URL = "entry_url";

    // Properties
    private static final String PROPERTY_TARGET_DEFAULT_VALUE = "entryUrl.create.defaultValue.target";
    private static final String PROPERTY_DISPLAY_PROPERTIES_DEFAULT_VALUE = "entryUrl.create.defaultValue.displayProperties";
    private static final String PROPERTY_LINK_PROPERTIES_DEFAULT_VALUE = "entryUrl.create.defaultValue.linkProperties";

    // Parameters
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_TARGET_FRAMENAME = "target_framename";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_URL = "url";
    private static final String PARAMETER_UPDATE_IMAGE = "update_image";
    private static final String PARAMETER_IMAGE = "image";
    private static final String PARAMETER_DISPLAY_PROPERTIES = "display_properties";
    private static final String PARAMETER_LINK_PROPERTIES = "link_properties";

    // Attributes
    private String _strTitle;
    private String _strTarget;
    private String _strDescription;
    private String _strUrl;
    private byte [ ] _bytesImage;
    private String _strImageMimeType;
    private EntryUrlDisplayProperties _entryUrlDisplayProperties;
    private EntryUrlLinkProperties _entryUrlLinkProperties;

    /**
     * @param strDescription
     *            the _strDescription to set
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     * @return the _strDescription
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * @return the url
     */
    public String getUrl( )
    {
        return _strUrl;
    }

    /**
     * @param strUrl
     *            the url to set
     */
    public void setUrl( String strUrl )
    {
        this._strUrl = strUrl;
    }

    /**
     * @return the image
     */
    public byte [ ] getImage( )
    {
        return _bytesImage;
    }

    /**
     * @param image
     *            the image to set
     */
    public void setImage( byte [ ] image )
    {
        this._bytesImage = image;
    }

    /**
     * @return the imageMimeType
     */
    public String getImageMimeType( )
    {
        return _strImageMimeType;
    }

    /**
     * @param strImageMimeType
     *            the imageMimeType to set
     */
    public void setImageMimeType( String strImageMimeType )
    {
        this._strImageMimeType = strImageMimeType;
    }

    /**
     * @return the entryUrlDisplayProperties
     */
    public EntryUrlDisplayProperties getEntryUrlDisplayProperties( )
    {
        return _entryUrlDisplayProperties;
    }

    /**
     * @param entryUrlDisplayProperties
     *            the entryUrlDisplayProperties to set
     */
    public void setEntryUrlDisplayProperties( EntryUrlDisplayProperties entryUrlDisplayProperties )
    {
        this._entryUrlDisplayProperties = entryUrlDisplayProperties;
    }

    /**
     * @return the entryUrlLinkProperties
     */
    public EntryUrlLinkProperties getEntryUrlLinkProperties( )
    {
        return _entryUrlLinkProperties;
    }

    /**
     * @param entryUrlLinkProperties
     *            the entryUrlLinkProperties to set
     */
    public void setEntryUrlLinkProperties( EntryUrlLinkProperties entryUrlLinkProperties )
    {
        this._entryUrlLinkProperties = entryUrlLinkProperties;
    }

    @Override
    public EntryUrl clone( ) throws CloneNotSupportedException
    {
        EntryUrl entryUrl = new EntryUrl( );
        entryUrl.setDescription( getDescription( ) );
        entryUrl.setEntryType( getEntryType( ) );
        entryUrl.setEntryUrlDisplayProperties( getEntryUrlDisplayProperties( ) );
        entryUrl.setEntryUrlLinkProperties( getEntryUrlLinkProperties( ) );
        entryUrl.setImage( getImage( ) );
        entryUrl.setImageMimeType( getImageMimeType( ) );
        entryUrl.setIdOrder( getIdOrder( ) );
        entryUrl.setIdParent( getIdParent( ) );
        entryUrl.setIdQuicklinks( getIdQuicklinks( ) );
        entryUrl.setTitle( getTitle( ) );
        entryUrl.setUrl( getUrl( ) );
        entryUrl.setTarget( getTarget( ) );

        return entryUrl;
    }

    @Override
    public String getHtml( Plugin plugin, Locale locale )
    {
        HashMap<String, Object> model = new HashMap<>( );

        model.put( MARK_ENTRY_URL, this );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DISPLAY, locale, model );

        return template.getHtml( );
    }

    @Override
    public String getTitle( )
    {
        return _strTitle;
    }

    @Override
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * Get the target
     * 
     * @return The target
     */
    public String getTarget( )
    {
        return _strTarget;
    }

    /**
     * Set the target
     * 
     * @param strTarget
     */
    public void setTarget( String strTarget )
    {
        this._strTarget = strTarget;
    }

    @Override
    public String setSpecificParameters( HttpServletRequest request )
    {
        String strTarget = request.getParameter( PARAMETER_TARGET );
        String strTargetFramename = request.getParameter( PARAMETER_TARGET_FRAMENAME );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strUrl = request.getParameter( PARAMETER_URL );
        String strUpdateImage = request.getParameter( PARAMETER_UPDATE_IMAGE );
        String strDisplayProperties = request.getParameter( PARAMETER_DISPLAY_PROPERTIES );

        String strLinkProperties = request.getParameter( PARAMETER_LINK_PROPERTIES );
        strLinkProperties = ( strLinkProperties == null ) ? EMPTY_STRING : strLinkProperties;

        boolean bUpdateImage = StringUtils.isNotEmpty( strUpdateImage );

        // Check Target
        if ( StringUtils.isEmpty( strTarget ) && StringUtils.isEmpty( strTargetFramename ) )
        {
            return Messages.MANDATORY_FIELDS;
        }

        if ( strTarget.equals( EMPTY_STRING ) )
        {
            setTarget( strTargetFramename );
        }
        else
        {
            setTarget( strTarget );
        }

        // Check description
        if ( StringUtils.isNotEmpty( strDescription ) )
        {
            this.setDescription( strDescription );
        }

        if ( StringUtils.isEmpty( strUrl ) || ( strDisplayProperties == null ) || !strDisplayProperties.matches( REGEX_ID )
                || !strLinkProperties.matches( REGEX_ID ) )
        {
            return Messages.MANDATORY_FIELDS;
        }

        int nDisplayProperties = Integer.parseInt( strDisplayProperties );
        int nLinkProperties = Integer.parseInt( strLinkProperties );

        setUrl( strUrl );

        // Check image
        if ( bUpdateImage )
        {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            FileItem item = mRequest.getFile( PARAMETER_IMAGE );
            setImage( item.get( ) );
            setImageMimeType( item.getContentType( ) );
        }

        // Check display properties
        EntryUrlDisplayProperties entryUrlDisplayProperties = EntryUrlDisplayProperties.getByValue( nDisplayProperties );

        // Check link properties
        EntryUrlLinkProperties entryUrlLinkProperties = EntryUrlLinkProperties.getByValue( nLinkProperties );

        if ( ( entryUrlDisplayProperties == null ) || ( entryUrlLinkProperties == null ) )
        {
            return Messages.MANDATORY_FIELDS;
        }

        setEntryUrlDisplayProperties( entryUrlDisplayProperties );
        setEntryUrlLinkProperties( entryUrlLinkProperties );

        return null;
    }

    @Override
    public void getSpecificParameters( HttpServletRequest request, Map<String, Object> model, Plugin plugin )
    {
        int nDisplayPropertiesDefaultValue = AppPropertiesService.getPropertyInt( PROPERTY_DISPLAY_PROPERTIES_DEFAULT_VALUE, DISPLAY_PROPERTIES_DEFAULT_VALUE );
        int nLinkPropertiesDefaultValue = AppPropertiesService.getPropertyInt( PROPERTY_LINK_PROPERTIES_DEFAULT_VALUE, LINK_PROPERTIES_DEFAULT_VALUE );

        model.put( MARK_DISPLAY_PROPERTIES_LIST, EntryUrlDisplayProperties.getReferenceList( ) );
        model.put( MARK_LINK_PROPERTIES_LIST, EntryUrlLinkProperties.getReferenceList( ) );
        model.put( MARK_TARGET_DEFAULT_VALUE, AppPropertiesService.getProperty( PROPERTY_TARGET_DEFAULT_VALUE, TARGET_DEFAULT_VALUE ) );
        model.put( MARK_DISPLAY_PROPERTIES_DEFAULT_VALUE, nDisplayPropertiesDefaultValue );
        model.put( MARK_LINK_PROPERTIES_DEFAULT_VALUE, nLinkPropertiesDefaultValue );
    }

    /**
     * Get the image resource for the entry url
     * 
     * @return The {@link ImageResource} object
     */
    public ImageResource getImageResource( )
    {
        ImageResource imageResource = new ImageResource( );
        imageResource.setImage( getImage( ) );
        imageResource.setMimeType( getImageMimeType( ) );

        return imageResource;
    }

    /**
     * Get the image Url
     * 
     * @return The image Url
     */
    public String getImageUrl( )
    {
        return EntryUrlService.getResourceImageEntryUrl( getId( ) );
    }

    @Override
    public void createSpecificParameters( Plugin plugin )
    {
        EntryUrlHome.create( this, plugin );
    }

    @Override
    public void loadSpecificParameters( Plugin plugin )
    {
        EntryUrlHome.load( this, plugin );
    }

    @Override
    public void removeSpecificParameters( Plugin plugin )
    {
        EntryUrlHome.remove( getId( ), plugin );
    }

    @Override
    public void updateSpecificParameters( Plugin plugin )
    {
        EntryUrlHome.update( this, plugin );
    }
}
